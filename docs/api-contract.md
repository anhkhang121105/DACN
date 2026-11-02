# API contract – Hệ thống phân công giảng dạy (FCAP)

| | |
|---|---|
| **contractVersion** | 1.0 |
| **Ngày chốt** | 02/11/2026 |
| **Người giữ file** | TV1 (backend). TV2 (frontend) dựng mock và fixture từ file này. |
| **Đường dẫn** | `docs/api-contract.md` |

**Quy tắc đổi contract:** ai muốn đổi thì nhắn người kia trước, sửa file này, tăng `contractVersion` và ghi vào mục 9. Từ 05/11 không đổi nếu chưa báo. Nếu code và file này lệch nhau thì file này đúng cho đến khi hai người thống nhất sửa.

---

## 1. Quy ước chung

- Mọi route có tiền tố `/api`. Dữ liệu JSON UTF-8. CORS cho `http://localhost:5173` (Vite).
- Gửi token ở header `Authorization: Bearer <token>`. Mọi route cần token, trừ `POST /auth/login` và `GET /ping`.
- **Plan = một học kỳ.** `{hocKy}` trong URL là `MaHocKy` (ví dụ `HK1_2627`). Không có bảng plan riêng.
- **Tên tham số trong URL** theo ERD: `hocKy`, `maBoMon`, `maNhomHoc`. **Tên trường trong JSON** dùng tiếng Anh (`lecturerId`, `sectionId`, `departmentId`...).
- ID là chuỗi (`GV01`, `BM_A`, `INT1007-HK1_2627-01`). Riêng `userId` và `runId` là số.
- **Mã lớp** (`id` của lớp) do server sinh khi tạo: `{subjectId}-{hocKy}-{nn}`, `nn` là số thứ tự 2 chữ số theo môn và học kỳ.
- **Ca học:** `"slot": {"day": 2, "shift": 1}`. `day` từ 2 đến 7 (thứ Hai đến thứ Bảy), `shift` từ 1 đến 5. Trong DB là cột `Thu`, `Ca`.
- **Giờ:** số thực một chữ số thập phân (`45.0`).
- **Thời gian:** ISO-8601 không kèm múi giờ (`2026-11-02T08:00:00`), múi giờ máy chủ Asia/Ho_Chi_Minh.
- **Danh sách:** lớp học nằm trong `sections`; các danh sách khác nằm trong `items`.
- **Token (JWT)** chứa: `sub` (userId), `role`, `lecturerId`, `departmentId`. `departmentId` lấy từ `GiangVien.MaBoMon` của tài khoản. TLĐT và BCN không có `lecturerId` và `departmentId` (giá trị `null`).
- **Không tin dữ liệu do client gửi về quyền:** vai trò, bộ môn, trạng thái luôn lấy từ token và DB.

### Dạng lỗi chuẩn

```json
{
  "status": 422,
  "code": "RULE_VIOLATION",
  "message": "Vi phạm luật phân công",
  "details": [
    {"ruleCode": "SLOT_CONFLICT", "sectionId": "INT1007-HK1_2627-01",
     "lecturerId": "GV01", "message": "GV01 đã dạy INT1008-HK1_2627-02 cùng ca"}
  ]
}
```

`details` luôn là mảng (rỗng nếu không có). Với `RULE_VIOLATION`, `details` liệt kê **tất cả** vi phạm tìm thấy, không chỉ vi phạm đầu tiên.

---

## 2. Vai trò và quyền

Giá trị `role`: `TLDT` (Trợ lý đào tạo), `GV` (Giảng viên), `TBM` (Trưởng bộ môn), `BCN` (Ban chủ nhiệm Khoa).

| Route | TLDT | GV | TBM | BCN |
|---|---|---|---|---|
| `POST /auth/login`, `GET /ping` | mọi người | | | |
| `GET /me` | ✓ | ✓ | ✓ | ✓ |
| `GET /plans/{hocKy}` | ✓ | ✓ | ✓ | ✓ |
| `GET /lecturers`, `GET /subjects` | ✓ | | ✓ | ✓ |
| `GET /plans/{hocKy}/sections` | ✓ | ✓ | ✓ | ✓ |
| `POST /plans/{hocKy}/sections`, `PUT /sections/{maNhomHoc}` | ✓ | | | |
| `PUT /plans/{hocKy}/registration` | ✓ | | | |
| `GET`/`PUT /me/preferences` | | ✓ | | |
| `GET`/`PUT /me/availability` | | ✓ | | |
| `GET /me/assignments` | | ✓ | | |
| `GET /plans/{hocKy}/board` | | | bộ môn mình | tất cả |
| `PUT`/`DELETE /sections/{maNhomHoc}/assignment` | | | lớp bộ môn mình | |
| `POST /plans/{hocKy}/suggest`, `/apply` | | | bộ môn mình | |
| `POST .../departments/{maBoMon}/submit` | | | đúng bộ môn mình | |
| `GET .../departments/{maBoMon}/validation` | | | đúng bộ môn mình | ✓ |
| `POST .../departments/{maBoMon}/return`, `/approve` | | | | ✓ |
| `GET .../departments/{maBoMon}/export.xlsx` | | | | ✓ |

(`...` là `/plans/{hocKy}`.) Sai vai trò: `403 FORBIDDEN_ROLE`. Đúng vai trò nhưng sai phạm vi (TBM sửa lớp hoặc nộp bộ môn khác, GV sửa của người khác): `403 FORBIDDEN_SCOPE`.

---

## 3. Giá trị cố định (enum)

| Tên | Giá trị |
|---|---|
| Trạng thái bộ môn (`KeHoachBoMon.TrangThai`) | `DRAFT`, `SUBMITTED`, `APPROVED`, `RETURNED` |
| Trạng thái solver | `OPTIMAL`, `FEASIBLE`, `INFEASIBLE`, `UNKNOWN`, `MODEL_INVALID` |
| Nguồn phân công (`source`) | `MANUAL`, `NGUYEN_VONG`, `SOLVER` |
| Mã luật (`ruleCode`) | `NOT_ELIGIBLE`, `SLOT_CONFLICT`, `LECTURER_BUSY`, `OVER_CAP`, `SECTION_PINNED` |

**Định nghĩa các luật** (BRE, tất cả là ràng buộc cứng):

| ruleCode | Vi phạm khi |
|---|---|
| `NOT_ELIGIBLE` | GV không thuộc bộ môn của môn và cũng không có trong bảng `GiangVienMonHoc` của môn đó |
| `SLOT_CONFLICT` | GV có hai lớp cùng `slot` trong một học kỳ |
| `LECTURER_BUSY` | Ca của lớp nằm trong ca bận của GV (`LichBan`) |
| `OVER_CAP` | Tổng giờ các lớp được giao của GV trong học kỳ lớn hơn `SemesterCap` |
| `SECTION_PINNED` | Đổi GV hoặc hủy phân công của lớp đang ghim (phải bỏ ghim trước) |

Số lớp chưa có GV **không** phải `ruleCode`; nó được báo riêng bằng `INCOMPLETE_ASSIGNMENT`.

**Điểm phạt nguyện vọng** (dùng cho `penalty`): lớp GV xếp hạng 1 = 0 điểm, hạng 2 = 1 điểm, hạng 3 = 2 điểm...; lớp GV không đăng ký = 5 điểm. Điểm của một bảng phân công là tổng các điểm đó.

---

## 4. Mã lỗi và thứ tự kiểm tra

**Thứ tự kiểm tra ở server** (để test ra kết quả cố định): JSON hợp lệ (400) → đăng nhập (401) → vai trò (403) → tồn tại (404) → phạm vi (403) → trạng thái bộ môn/đăng ký (409) → dữ liệu và luật (422).

Nguyên tắc: **409** là trạng thái hệ thống không cho phép dù dữ liệu đúng; **422** là dữ liệu hoặc luật sai.

| HTTP | `code` | Khi nào |
|---|---|---|
| 400 | `BAD_REQUEST` | JSON hỏng, tham số sai kiểu |
| 401 | `UNAUTHENTICATED` | Thiếu, sai hoặc hết hạn token |
| 401 | `BAD_CREDENTIALS` | Sai tài khoản hoặc mật khẩu (chỉ ở login; không cho biết tài khoản có tồn tại hay không) |
| 403 | `FORBIDDEN_ROLE` | Vai trò không được gọi route này |
| 403 | `FORBIDDEN_SCOPE` | Đúng vai trò nhưng sai phạm vi |
| 404 | `NOT_FOUND` | Học kỳ, bộ môn, lớp, GV hoặc run không tồn tại |
| 409 | `DEPT_LOCKED` | Bộ môn đang `SUBMITTED` hoặc `APPROVED` (`details[0].status` ghi rõ) |
| 409 | `DEPT_NOT_SUBMITTED` | Trả về hoặc duyệt khi bộ môn không ở `SUBMITTED` |
| 409 | `DEPT_NOT_APPROVED` | Xuất Excel khi bộ môn chưa `APPROVED` |
| 409 | `REGISTRATION_CLOSED` | GV sửa nguyện vọng hoặc ca bận khi `registrationOpen = false` |
| 409 | `STALE_PREVIEW` | Áp dụng bản gợi ý đã cũ (`inputRevision` khác `revision` hiện tại) |
| 422 | `RULE_VIOLATION` | Vi phạm luật (xem mục 3); `details[].ruleCode` ghi từng vi phạm |
| 422 | `INCOMPLETE_ASSIGNMENT` | Nộp hoặc duyệt khi còn lớp chưa có GV; `details[].sectionId` liệt kê các lớp thiếu |
| 422 | `NO_USABLE_SOLUTION` | Áp dụng khi run là `UNKNOWN`, `INFEASIBLE` hoặc `MODEL_INVALID` |
| 422 | `INVALID_INPUT` | Thiếu trường, giá trị ngoài khoảng (ví dụ `day: 9`) |
| 500 | `INTERNAL_ERROR` | Lỗi server; không trả stack trace |

---

## 5. Luồng duyệt theo bộ môn

Mỗi bộ môn trong một học kỳ có một dòng `KeHoachBoMon(MaHocKy, MaBoMon)`. Không có trạng thái duyệt chung cho cả học kỳ.

```
DRAFT --(TBM nộp)--> SUBMITTED --(BCN trả về)--> RETURNED --(TBM nộp lại)--> SUBMITTED
                         |
                         +--(BCN duyệt)--> APPROVED   (kết thúc, không mở khóa)
```

| Trạng thái | Ý nghĩa | Ghi được? |
|---|---|---|
| `DRAFT` | Đang lập | Có |
| `SUBMITTED` | TBM đã nộp, chờ BCN | Không |
| `RETURNED` | BCN trả về, có `returnReason` | Có (như DRAFT) |
| `APPROVED` | BCN đã duyệt | Không |

**Chuyển trạng thái**

| Hành động | Ai | Từ → Đến | Điều kiện |
|---|---|---|---|
| Nộp | TBM | `DRAFT`/`RETURNED` → `SUBMITTED` | Đủ lớp (`INCOMPLETE_ASSIGNMENT`), 0 lỗi luật (`RULE_VIOLATION`); xóa `returnReason` |
| Trả về | BCN | `SUBMITTED` → `RETURNED` | Bắt buộc có `reason`; lưu vào `LyDoTraVe` |
| Duyệt | BCN | `SUBMITTED` → `APPROVED` | Đủ lớp, 0 lỗi, kiểm tra cả xung đột chéo bộ môn |

**Điều kiện cho từng thao tác ghi**

| Thao tác | Điều kiện (không đạt thì trả) |
|---|---|
| TLĐT tạo/sửa lớp | Bộ môn của môn học đang `DRAFT`/`RETURNED` (`DEPT_LOCKED`) |
| TLĐT bật/tắt đăng ký | Không phụ thuộc trạng thái bộ môn |
| GV sửa nguyện vọng, ca bận | `registrationOpen = true` (`REGISTRATION_CLOSED`) |
| TBM gán, hủy, suggest, apply | Bộ môn mình `DRAFT`/`RETURNED` (`DEPT_LOCKED`); lớp thuộc bộ môn mình (`FORBIDDEN_SCOPE`) |
| TBM nộp | Như bảng chuyển trạng thái |
| BCN trả về, duyệt | Bộ môn đang `SUBMITTED` (`DEPT_NOT_SUBMITTED`) |
| Xuất Excel | Bộ môn `APPROVED` (`DEPT_NOT_APPROVED`) |

TBM gán lớp không phụ thuộc đăng ký đã đóng hay chưa.

### Biến `revision`

Là cột `Revision` của bảng `HocKy`, kiểu số nguyên. **Tăng 1** mỗi khi: tạo/sửa lớp, đổi nguyện vọng của một GV, đổi ca bận, gán/hủy/ghim phân công (của bất kỳ bộ môn nào), áp dụng gợi ý (tăng đúng 1 cho cả lần áp dụng). **Không tăng** khi: bật/tắt đăng ký, nộp, trả về, duyệt. Nếu sửa `SemesterCap` bằng script thì phải tăng `Revision` thủ công.

---

## 6. Chi tiết route

### 6.1 Đăng nhập và người dùng

#### `GET /ping`
Không cần token. `200 {"status":"ok"}`.

#### `POST /auth/login`
Không cần token.

```json
{"username": "tbm_a", "password": "123456"}
```

`200`:

```json
{"token": "eyJ...", "role": "TBM", "userId": 3,
 "lecturerId": "GV01", "departmentId": "BM_A", "hoTen": "Giảng viên 01"}
```

Với TLĐT/BCN: `lecturerId`, `departmentId`, `hoTen` có thể là `null`. Lỗi: `401 BAD_CREDENTIALS`, `422 INVALID_INPUT` (thiếu trường), `400 BAD_REQUEST`.

#### `GET /me`
`200`: `{"userId":3,"username":"tbm_a","role":"TBM","lecturerId":"GV01","departmentId":"BM_A","hoTen":"Giảng viên 01"}`. Lỗi: `401 UNAUTHENTICATED`.

---

### 6.2 Học kỳ và danh mục

#### `GET /plans/{hocKy}`
Mọi vai trò.

```json
{"hocKy": "HK1_2627", "namHoc": "2026-2027", "registrationOpen": false, "revision": 14,
 "departments": [
   {"departmentId": "BM_A", "status": "SUBMITTED", "returnReason": null},
   {"departmentId": "BM_B", "status": "RETURNED", "returnReason": "Lớp INT1007-HK1_2627-05 trùng ca"}
 ]}
```

Lỗi: `404 NOT_FOUND`.

#### `GET /lecturers?maBoMon=BM_A`
TLĐT, TBM, BCN. `maBoMon` không bắt buộc.

```json
{"items": [{"id": "GV01", "name": "Giảng viên 01", "departmentId": "BM_A",
            "status": "CHINH_THUC", "semesterCap": 180.0, "semesterTarget": 150.0}]}
```

#### `GET /subjects?maBoMon=BM_A`
TLĐT, TBM, BCN. `maBoMon` không bắt buộc.

```json
{"items": [{"id": "INT1007", "name": "Cơ sở dữ liệu", "credits": 3,
            "theoryPeriods": 30, "practicePeriods": 15, "departmentId": "BM_A",
            "elective": false}]}
```

---

### 6.3 Lớp học phần

Đối tượng lớp (`section`):

```json
{"id": "INT1007-HK1_2627-01", "name": "Nhóm 01", "subjectId": "INT1007",
 "subject": "Cơ sở dữ liệu", "departmentId": "BM_A", "size": 45,
 "slot": {"day": 2, "shift": 1}, "hours": 45.0, "roomId": null}
```

#### `GET /plans/{hocKy}/sections?maBoMon=BM_A`
Mọi vai trò. `maBoMon` không bắt buộc. `200 {"revision": 14, "sections": [ ...section... ]}`.

#### `POST /plans/{hocKy}/sections`
TLĐT.

```json
{"subjectId": "INT1007", "name": "Nhóm 01", "size": 45,
 "slot": {"day": 2, "shift": 1}, "hours": 45.0, "roomId": null}
```

`hours` không bắt buộc: nếu thiếu, server dùng `theoryPeriods + practicePeriods` của môn. `201` trả về `section` vừa tạo kèm `revision`. Lỗi: `409 DEPT_LOCKED` (bộ môn của môn đang khóa), `422 INVALID_INPUT` (môn không tồn tại, `slot` ngoài khoảng), `404 NOT_FOUND`.

#### `PUT /sections/{maNhomHoc}`
TLĐT. Body có các trường được sửa: `name`, `size`, `slot`, `hours`, `roomId` (gửi trường nào sửa trường đó). `200` trả về `section` mới kèm `revision`. Nếu lớp đã có GV và đổi `slot` hoặc `hours` làm vi phạm luật thì `422 RULE_VIOLATION` và **không lưu**. Lỗi khác: `409 DEPT_LOCKED`, `422 INVALID_INPUT`, `404 NOT_FOUND`.

#### `PUT /plans/{hocKy}/registration`
TLĐT.

```json
{"open": true}
```

`200 {"hocKy": "HK1_2627", "registrationOpen": true}`. Không đổi `revision`.

---

### 6.4 Giảng viên: nguyện vọng, ca bận, lịch phân công

Các route `/me/*` luôn là của người đang đăng nhập (lấy `lecturerId` từ token) và **bắt buộc** có `?hocKy=`.

#### `GET /me/preferences?hocKy=HK1_2627`
GV.

```json
{"hocKy": "HK1_2627", "items": [
  {"sectionId": "INT1007-HK1_2627-01", "rank": 1},
  {"sectionId": "INT1008-HK1_2627-02", "rank": 2}]}
```

#### `PUT /me/preferences?hocKy=HK1_2627`
GV. **Thay thế toàn bộ** nguyện vọng của GV trong học kỳ.

```json
{"items": [{"sectionId": "INT1007-HK1_2627-01", "rank": 1},
           {"sectionId": "INT1008-HK1_2627-02", "rank": 2}]}
```

Quy tắc: tối đa 8 mục; `rank` là 1, 2, 3... liên tục không trùng; `sectionId` phải thuộc học kỳ. GV được đăng ký cả lớp mình không đủ điều kiện dạy (luật `NOT_ELIGIBLE` chặn ở bước gán). `200` trả về như `GET` kèm `revision`. Lỗi: `409 REGISTRATION_CLOSED`, `422 INVALID_INPUT`, `404 NOT_FOUND` (học kỳ).

#### `GET /me/availability?hocKy=HK1_2627`
GV. `200 {"hocKy": "HK1_2627", "busy": [{"day": 2, "shift": 3}, {"day": 5, "shift": 1}]}`.

#### `PUT /me/availability?hocKy=HK1_2627`
GV. **Thay thế toàn bộ** ca bận. Body: `{"busy": [{"day": 2, "shift": 3}]}`. `200` trả về như `GET` kèm `revision`. Lỗi: `409 REGISTRATION_CLOSED`, `422 INVALID_INPUT`.

#### `GET /me/assignments?hocKy=HK1_2627`
GV.

```json
{"hocKy": "HK1_2627", "lecturerId": "GV02", "semesterCap": 180.0,
 "semesterTarget": 150.0, "totalHours": 90.0,
 "assignments": [{"sectionId": "INT1007-HK1_2627-01", "subject": "Cơ sở dữ liệu",
                  "slot": {"day": 2, "shift": 1}, "hours": 45.0}]}
```

`totalHours` là tổng giờ các lớp đã được giao trong học kỳ. Giao diện hiển thị tiến độ `totalHours / semesterCap`.

---

### 6.5 Bảng phân công và gán

#### `GET /plans/{hocKy}/board?maBoMon=BM_A`
TBM (luôn là bộ môn mình; truyền `maBoMon` khác thì `403 FORBIDDEN_SCOPE`), BCN (`maBoMon` bắt buộc).

```json
{"revision": 14, "departmentId": "BM_A", "departmentStatus": "DRAFT",
 "sections": [{
   "id": "INT1007-HK1_2627-01", "name": "Nhóm 01", "subject": "Cơ sở dữ liệu",
   "departmentId": "BM_A", "slot": {"day": 2, "shift": 1}, "hours": 45.0, "pinned": false,
   "assignment": {"lecturerId": "GV01", "lecturerName": "Giảng viên 01", "source": "NGUYEN_VONG"},
   "preferences": [{"lecturerId": "GV01", "rank": 1}],
   "violations": []
 }]}
```

`assignment` là `null` nếu lớp chưa có GV. `violations` là các vi phạm hiện có của lớp (cùng dạng như trong lỗi).

#### `PUT /sections/{maNhomHoc}/assignment`
TBM. Gán hoặc đổi GV.

```json
{"lecturerId": "GV01", "pinned": false}
```

`200`:

```json
{"sectionId": "INT1007-HK1_2627-01", "lecturerId": "GV01", "pinned": false,
 "source": "NGUYEN_VONG", "revision": 15}
```

- `source` do server đặt: `NGUYEN_VONG` nếu GV có đăng ký lớp này, ngược lại `MANUAL`.
- Vi phạm luật cứng thì **không lưu**: `422 RULE_VIOLATION`, `details` liệt kê mọi vi phạm. Việc kiểm tra tính trên toàn học kỳ (kể cả phân công của bộ môn khác), vì GV có thể dạy chéo bộ môn.
- Lớp đang ghim: muốn đổi GV phải gửi `pinned: false` ở lần gọi trước; nếu đổi GV khi đang ghim thì `422 RULE_VIOLATION` với `SECTION_PINNED`.
- Lỗi khác: `403 FORBIDDEN_SCOPE`, `409 DEPT_LOCKED`, `404 NOT_FOUND`, `422 INVALID_INPUT` (GV không tồn tại).

#### `DELETE /sections/{maNhomHoc}/assignment`
TBM. Hủy phân công. `200 {"sectionId": "...", "revision": 16}`. Lớp đang ghim thì `422 RULE_VIOLATION` với `SECTION_PINNED`. Lỗi khác như `PUT`.

---

### 6.6 Gợi ý (solver)

Solver chỉ xử lý các lớp **không ghim** của bộ môn người gọi. Lớp đang ghim và phân công của bộ môn khác được coi là cố định (tính vào tải và ca của GV). Các phân công hiện có nhưng không ghim của bộ môn mình **có thể bị thay đổi** trong bản gợi ý.

#### `POST /plans/{hocKy}/suggest`
TBM.

```json
{"timeLimitSeconds": 5}
```

`timeLimitSeconds` không bắt buộc (mặc định 5, khoảng 1–30, ngoài khoảng thì `422 INVALID_INPUT`).

`200` (luôn là 200 kể cả khi `UNKNOWN`/`INFEASIBLE`, lúc đó `preview` rỗng):

```json
{"runId": 9, "status": "OPTIMAL", "elapsedMs": 420, "inputRevision": 15,
 "unassignedCount": 0, "unassignedSectionIds": [], "penalty": 7,
 "preview": [{"sectionId": "INT1008-HK1_2627-02", "lecturerId": "GV03"}]}
```

Server lưu một dòng `SolverRun` (status, thời gian, kết quả, `inputRevision`). **Chưa ghi gì** vào phân công. Lỗi: `403`, `409 DEPT_LOCKED`, `422 INVALID_INPUT`.

#### `POST /plans/{hocKy}/apply`
TBM. Client chỉ gửi `runId`, không gửi danh sách phân công.

```json
{"runId": 9}
```

Server làm theo thứ tự: run phải của bộ môn mình và học kỳ này (`404 NOT_FOUND` nếu không) → bộ môn còn ghi được (`409 DEPT_LOCKED`) → `inputRevision` bằng `revision` hiện tại (`409 STALE_PREVIEW`) → status là `OPTIMAL`/`FEASIBLE` (`422 NO_USABLE_SOLUTION`) → validate lại toàn bộ luật (`422 RULE_VIOLATION`) → ghi trong **một transaction** (thay các phân công không ghim của bộ môn bằng `preview`, `source = SOLVER`; lớp không còn trong `preview` thì bỏ phân công) và tăng `revision`.

`200 {"applied": 5, "revision": 16}`.

---

### 6.7 Nộp, trả về, kiểm tra, duyệt

#### `POST /plans/{hocKy}/departments/{maBoMon}/submit`
TBM, đúng bộ môn mình (sai thì `403 FORBIDDEN_SCOPE`). Không có body.
`200 {"departmentId": "BM_A", "status": "SUBMITTED"}`.
Lỗi: `409 DEPT_LOCKED`, `422 INCOMPLETE_ASSIGNMENT`, `422 RULE_VIOLATION`.

#### `GET /plans/{hocKy}/departments/{maBoMon}/validation`
TBM (bộ môn mình), BCN.

```json
{"revision": 16, "departmentId": "BM_A", "totalSections": 12, "assigned": 12,
 "unassignedSectionIds": [], "violations": []}
```

`violations` cùng dạng như trong lỗi; kiểm tra tính trên toàn học kỳ.

#### `POST /plans/{hocKy}/departments/{maBoMon}/return`
BCN.

```json
{"reason": "Lớp INT1007-HK1_2627-05 trùng ca"}
```

`200 {"departmentId": "BM_B", "status": "RETURNED", "returnReason": "Lớp INT1007-HK1_2627-05 trùng ca"}`.
Lỗi: `409 DEPT_NOT_SUBMITTED`, `422 INVALID_INPUT` (thiếu `reason`).

#### `POST /plans/{hocKy}/departments/{maBoMon}/approve`
BCN. Không có body.
`200 {"departmentId": "BM_A", "status": "APPROVED"}`.
Lỗi: `409 DEPT_NOT_SUBMITTED`, `422 INCOMPLETE_ASSIGNMENT`, `422 RULE_VIOLATION`.

---

### 6.8 Xuất Excel

#### `GET /plans/{hocKy}/departments/{maBoMon}/export.xlsx`
BCN. Chỉ bộ môn `APPROVED` (`409 DEPT_NOT_APPROVED`).

- `Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- `Content-Disposition: attachment; filename="phancong_HK1_2627_BM_A.xlsx"`
- Một sheet tên `PhanCong`, **không gộp ô, không trang trí**, một dòng tiêu đề rồi mỗi lớp một dòng, sắp theo mã lớp.
- Cột theo thứ tự: `STT`, `Mã lớp`, `Tên môn`, `Nhóm`, `Thứ`, `Ca`, `Sĩ số`, `Giờ`, `Mã GV`, `Họ tên GV`.

**Frontend:** token nằm ở header nên phải tải bằng Axios `responseType: 'blob'` rồi tạo link tải; không dùng thẻ `<a href>` trực tiếp.

---

## 7. Fixture bắt buộc cho mock (TV2)

Mỗi dòng là một file/mục fixture. `apiClient` ở chế độ mock trả đúng các phản hồi này.

| Route | Fixture cần có |
|---|---|
| `POST /auth/login` | OK (4 vai trò), `401 BAD_CREDENTIALS`, `422 INVALID_INPUT` |
| `GET /plans/{hocKy}` | Mỗi bộ môn một trạng thái khác nhau (đủ `DRAFT`, `SUBMITTED`, `RETURNED` có lý do, `APPROVED`) |
| `GET .../board` | Bộ môn `DRAFT` có lớp chưa gán + có vi phạm; bộ môn `SUBMITTED`; bộ môn `APPROVED` |
| `PUT .../assignment` | `200`; `422` cho từng `ruleCode` (5 loại); `409 DEPT_LOCKED`; `403 FORBIDDEN_SCOPE` |
| `POST .../suggest` | `OPTIMAL`; `FEASIBLE`; `UNKNOWN` (preview rỗng); `INFEASIBLE` |
| `POST .../apply` | `200`; `409 STALE_PREVIEW`; `409 DEPT_LOCKED`; `422 NO_USABLE_SOLUTION` |
| `.../submit` | `200`; `409 DEPT_LOCKED`; `422 INCOMPLETE_ASSIGNMENT`; `422 RULE_VIOLATION` |
| `.../return`, `.../approve` | `200`; `409 DEPT_NOT_SUBMITTED`; `422 INCOMPLETE_ASSIGNMENT`; `422 RULE_VIOLATION` (chỉ approve) |
| `.../validation` | Sạch; có vi phạm; có lớp chưa gán |
| `.../export.xlsx` | Một file mẫu hợp lệ; `409 DEPT_NOT_APPROVED` |
| `/me/preferences`, `/me/availability` | `200`; `409 REGISTRATION_CLOSED`; `422 INVALID_INPUT` |
| Chung | `401 UNAUTHENTICATED`, `403 FORBIDDEN_ROLE`, `404 NOT_FOUND`, `500 INTERNAL_ERROR` |

---

## 8. Việc kiểm tra liên quan (đối chiếu với bộ test)

| Bộ test | Mã lỗi/route cần khớp |
|---|---|
| T01 | `401 UNAUTHENTICATED`, `403 FORBIDDEN_ROLE` |
| T02–T04 | `422 RULE_VIOLATION` với `NOT_ELIGIBLE`, `SLOT_CONFLICT`/`LECTURER_BUSY`, `OVER_CAP`; không lưu |
| T07, T10 | `NO_USABLE_SOLUTION`, `STALE_PREVIEW` |
| T08, T16 | `422 INCOMPLETE_ASSIGNMENT` |
| T09, T15 | `409 DEPT_LOCKED` |
| T11 | `export.xlsx`; `409 DEPT_NOT_APPROVED` |
| T13 | `409 DEPT_NOT_SUBMITTED` |
| T14 | `RETURNED` → sửa được và nộp lại được; bộ môn khác đang `SUBMITTED` vẫn `DEPT_LOCKED` |

---

## 9. Nhật ký thay đổi

| Phiên bản | Ngày | Nội dung |
|---|---|---|
| 1.0 | 02/11/2026 | Bản đầu. Luồng duyệt theo `KeHoachBoMon`. Thêm `GET /me/preferences`, `GET /me/availability`; các route `/me/*` dùng `?hocKy=`; danh sách lọc theo `?maBoMon=`. |

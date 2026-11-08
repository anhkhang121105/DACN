# Phân công giảng viên

Hệ thống hỗ trợ lập kế hoạch mở lớp, quản lý nguyện vọng và phân công giảng viên cho Khoa Công nghệ thông tin.

Project gồm:

- Backend: Spring Boot 3.3.5, Java 21, Maven, MySQL, Flyway.
- Frontend: React 18, Vite, Tailwind CSS.
- Migration và dữ liệu mẫu: `backend/src/main/resources/db/migration`.

## Yêu cầu môi trường

- Windows PowerShell.
- JDK 21.
- Maven 3.9+.
- Node.js và npm.
- MySQL 8.x hoặc XAMPP MySQL.

Kiểm tra công cụ:

```powershell
java -version
mvn -version
node --version
npm --version
```

Project yêu cầu Java 21. MySQL cần chạy ở cổng `3306`, trừ khi bạn cấu hình cổng khác bằng biến môi trường.

## Chạy nhanh trên Windows

Bật MySQL trước, sau đó mở PowerShell tại thư mục gốc project:

```powershell
.\start-web.ps1
```

Hoặc nhấp đúp vào `start-web.bat`.

Script sẽ:

1. Tìm `mysql.exe` trong `PATH`, XAMPP hoặc MySQL Server.
2. Kiểm tra kết nối MySQL và tự tạo database `phancong_gv` nếu chưa có.
3. Build backend nếu JAR chưa tồn tại.
4. Chạy backend tại `http://localhost:8080`.
5. Cài dependencies cho frontend nếu chưa có.
6. Chạy frontend tại `http://localhost:5173` và mở trình duyệt.

Tài khoản demo:

```text
Tên đăng nhập: trolydt
Mật khẩu: 123456
```

Dừng frontend/backend:

```powershell
.\stop-web.ps1
```

Lệnh này dừng frontend và backend, không xóa hoặc dừng MySQL của XAMPP/MySQL Server.

## Cấu hình MySQL

### XAMPP hoặc MySQL không đặt mật khẩu

Không cần đặt thêm biến môi trường. Script sẽ thử mật khẩu rỗng.

### MySQL dùng mật khẩu `root`

Script sẽ tự thử mật khẩu `root` nếu `DB_PASSWORD` chưa được đặt.

### Cấu hình tùy chỉnh

Đặt biến môi trường trước khi chạy:

```powershell
$env:DB_HOST = "127.0.0.1"
$env:DB_PORT = "3306"
$env:DB_NAME = "phancong_gv"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "mật_khẩu_mysql"

.\start-web.ps1
```

Không commit mật khẩu thật vào Git.

## Chạy MySQL bằng Docker

Nếu máy có Docker Desktop, có thể chỉ chạy MySQL bằng Compose:

```powershell
docker compose up -d mysql
```

Docker tạo MySQL với:

```text
Host: 127.0.0.1
Port: 3306
Database: phancong_gv
Username: root
Password: root
```

Sau đó chạy backend và frontend thủ công:

```powershell
cd backend
mvn spring-boot:run
```

Mở một PowerShell khác:

```powershell
cd frontend
npm install
npm run dev
```

Dừng MySQL Docker:

```powershell
docker compose down
```

Volume `phancong_mysql_data` được giữ lại, nên `docker compose down` không xóa dữ liệu. Chỉ dùng lệnh sau nếu muốn xóa toàn bộ database Docker:

```powershell
docker compose down -v
```

## Database và migration

Khi backend khởi động, Flyway tự động chạy các migration theo thứ tự:

- `V1__schema.sql`: tạo bảng và view.
- `V2__seed_data.sql`: nạp dữ liệu mẫu.
- `V3__tham_so_luat.sql`: nạp tham số luật.
- `V4__add_lich_day.sql`: bổ sung cột lịch dạy.

Flyway lưu lịch sử trong bảng `flyway_schema_history`. Migration đã chạy sẽ không chạy lại, vì vậy khởi động lại ứng dụng không reset dữ liệu.

Kiểm tra nhanh với XAMPP không có mật khẩu:

```powershell
C:\xampp\mysql\bin\mysql.exe -u root --password= -D phancong_gv -e "SHOW TABLES;"
```

Nếu dùng MySQL có mật khẩu `root`:

```powershell
mysql -u root -proot -D phancong_gv -e "SHOW TABLES;"
```

Chỉ reset database khi bạn chắc chắn không cần dữ liệu hiện tại:

```sql
DROP DATABASE phancong_gv;
CREATE DATABASE phancong_gv CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Lần khởi động backend tiếp theo sẽ tạo lại schema và dữ liệu mẫu.

## Chạy riêng từng thành phần

### Backend

```powershell
cd backend
mvn spring-boot:run
```

Nếu MySQL không dùng mật khẩu mặc định:

```powershell
$env:DB_URL = "jdbc:mysql://127.0.0.1:3306/phancong_gv?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh&characterEncoding=UTF-8"
$env:DB_USERNAME = "root"
$env:DB_PASSWORD = "mật_khẩu_mysql"
mvn spring-boot:run
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

### Frontend

```powershell
cd frontend
npm install
npm run dev
```

Frontend proxy các request `/api` tới backend tại cổng `8080`.

Build frontend:

```powershell
npm run build
```

## Log và file sinh tự động

Log khi dùng script nằm trong thư mục `logs/`:

- `logs/backend.log`
- `logs/backend.err`
- `logs/frontend.log`
- `logs/frontend.err`

Các thư mục `target/`, `node_modules/`, `dist/` và các file log không được commit. Xem [`.gitignore`](.gitignore).

## Xử lý lỗi thường gặp

### Không tìm thấy `mysql.exe`

Cài MySQL/XAMPP hoặc thêm thư mục `bin` của MySQL vào `PATH`. Có thể đặt `DB_HOST`, `DB_PORT`, `DB_USERNAME` và `DB_PASSWORD` để dùng server khác.

### Không kết nối được MySQL

Kiểm tra MySQL đang chạy và cổng `3306` đang mở. Nếu dùng cổng khác:

```powershell
$env:DB_PORT = "3307"
.\start-web.ps1
```

### Backend không khởi động

Xem log:

```powershell
Get-Content .\logs\backend.err
Get-Content .\logs\backend.log
```

Kiểm tra lại Java 21 và thông tin đăng nhập MySQL.

### Vite báo lỗi `403 Restricted`

Chạy frontend từ đúng thư mục `frontend`, không mở trực tiếp file `index.html`:

```powershell
cd frontend
npm run dev
```

### Đã sửa backend nhưng ứng dụng vẫn dùng JAR cũ

Build lại backend:

```powershell
cd backend
mvn -DskipTests package
```

Sau đó chạy lại `start-web.ps1`.

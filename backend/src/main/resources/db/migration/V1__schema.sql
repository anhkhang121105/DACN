-- =====================================================================
-- HỆ THỐNG PHÂN CÔNG GIẢNG VIÊN - KHOA CNTT, ĐH SÀI GÒN
-- MySQL 8.0 | Schema 3NF
-- =====================================================================

-- 1. DANH MỤC NỀN
CREATE TABLE nam_hoc (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ten VARCHAR(20) NOT NULL UNIQUE,
  trang_thai ENUM('DANG_LAP','DA_KHOA') NOT NULL DEFAULT 'DANG_LAP'
);

CREATE TABLE hoc_ky (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nam_hoc_id INT NOT NULL,
  so_hk TINYINT NOT NULL CHECK (so_hk IN (1,2,3)),
  ngay_bd DATE, ngay_kt DATE,
  mo_dang_ky BOOLEAN NOT NULL DEFAULT FALSE,
  UNIQUE (nam_hoc_id, so_hk),
  FOREIGN KEY (nam_hoc_id) REFERENCES nam_hoc(id)
);

CREATE TABLE bo_mon (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ma VARCHAR(20) NOT NULL UNIQUE,
  ten VARCHAR(100) NOT NULL
);

-- 2. GIẢNG VIÊN & TÀI KHOẢN
CREATE TABLE giang_vien (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ma_cb VARCHAR(10) NOT NULL UNIQUE,
  ho_ten VARCHAR(100) NOT NULL,
  nam_sinh SMALLINT,
  hoc_vi VARCHAR(20),
  chuc_danh VARCHAR(20),
  loai ENUM('CO_HUU','MOI_GIANG','THINH_GIANG') NOT NULL,
  bo_mon_id INT NULL,
  don_vi_cong_tac VARCHAR(150),
  chuyen_nganh VARCHAR(100),
  la_tap_su BOOLEAN NOT NULL DEFAULT FALSE,
  dinh_muc_min DECIMAL(6,1) NOT NULL DEFAULT 200,
  dinh_muc_max DECIMAL(6,1) NOT NULL DEFAULT 350,
  FOREIGN KEY (bo_mon_id) REFERENCES bo_mon(id)
);

CREATE TABLE nguoi_dung (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) NOT NULL UNIQUE,
  password_hash VARCHAR(100) NOT NULL,
  vai_tro ENUM('TRO_LY','GIANG_VIEN','TRUONG_BO_MON','BCN_KHOA') NOT NULL,
  giang_vien_id INT NULL,
  FOREIGN KEY (giang_vien_id) REFERENCES giang_vien(id)
);

-- 3. CTĐT - MÔN HỌC
CREATE TABLE chuong_trinh_dao_tao (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ma VARCHAR(20) NOT NULL UNIQUE,
  ten VARCHAR(150) NOT NULL,
  he ENUM('DAI_TRA','CLC') NOT NULL DEFAULT 'DAI_TRA',
  khoa_tuyen VARCHAR(10) NOT NULL
);

CREATE TABLE khoi_kien_thuc (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ten VARCHAR(100) NOT NULL
);

CREATE TABLE mon_hoc (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ma_hp VARCHAR(10) NOT NULL UNIQUE,
  ten VARCHAR(150) NOT NULL,
  so_tc TINYINT NOT NULL,
  tiet_lt SMALLINT NOT NULL DEFAULT 0,
  tiet_bt SMALLINT NOT NULL DEFAULT 0,
  tiet_th SMALLINT NOT NULL DEFAULT 0,
  tiet_tc SMALLINT GENERATED ALWAYS AS (tiet_lt + tiet_bt + tiet_th) STORED,
  he_so_hp DECIMAL(4,2) NOT NULL DEFAULT 1.00,
  bo_mon_id INT NOT NULL,
  khoi_kien_thuc_id INT,
  FOREIGN KEY (bo_mon_id) REFERENCES bo_mon(id),
  FOREIGN KEY (khoi_kien_thuc_id) REFERENCES khoi_kien_thuc(id)
);

CREATE TABLE ctdt_mon_hoc (
  ctdt_id INT NOT NULL,
  mon_hoc_id INT NOT NULL,
  hk_du_kien TINYINT,
  PRIMARY KEY (ctdt_id, mon_hoc_id),
  FOREIGN KEY (ctdt_id) REFERENCES chuong_trinh_dao_tao(id),
  FOREIGN KEY (mon_hoc_id) REFERENCES mon_hoc(id)
);

CREATE TABLE giang_vien_mon_hoc (
  giang_vien_id INT NOT NULL,
  mon_hoc_id INT NOT NULL,
  co_the_day_lt BOOLEAN NOT NULL DEFAULT TRUE,
  co_the_day_th BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (giang_vien_id, mon_hoc_id),
  FOREIGN KEY (giang_vien_id) REFERENCES giang_vien(id),
  FOREIGN KEY (mon_hoc_id) REFERENCES mon_hoc(id)
);

-- 4. KẾ HOẠCH MỞ NHÓM & PHÂN CÔNG
CREATE TABLE ke_hoach_mo_lop (
  id INT AUTO_INCREMENT PRIMARY KEY,
  hoc_ky_id INT NOT NULL,
  mon_hoc_id INT NOT NULL,
  khoa_hien_thi VARCHAR(100),
  tong_sv INT NOT NULL,
  max_sv_nhom INT NOT NULL DEFAULT 50,
  so_nhom INT GENERATED ALWAYS AS (CEIL(tong_sv / max_sv_nhom)) STORED,
  ghi_chu VARCHAR(500),
  UNIQUE (hoc_ky_id, mon_hoc_id, khoa_hien_thi),
  FOREIGN KEY (hoc_ky_id) REFERENCES hoc_ky(id),
  FOREIGN KEY (mon_hoc_id) REFERENCES mon_hoc(id)
);

CREATE TABLE nhom_hoc_phan (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ke_hoach_id INT NOT NULL,
  so_nhom INT NOT NULL,
  loai ENUM('LT','TH','LT_TH') NOT NULL,
  so_tiet_thuc_hien DECIMAL(6,1) NOT NULL,
  he_so DECIMAL(4,2) NOT NULL,
  so_tiet_thuc_te DECIMAL(7,2) GENERATED ALWAYS AS (ROUND(so_tiet_thuc_hien * he_so, 2)) STORED,
  giang_vien_id INT NULL,
  nguon ENUM('CHUA','THU_CONG','CP_SAT') NOT NULL DEFAULT 'CHUA',
  is_fixed BOOLEAN NOT NULL DEFAULT FALSE,
  UNIQUE (ke_hoach_id, so_nhom, loai),
  FOREIGN KEY (ke_hoach_id) REFERENCES ke_hoach_mo_lop(id) ON DELETE CASCADE,
  FOREIGN KEY (giang_vien_id) REFERENCES giang_vien(id)
);

CREATE TABLE nguyen_vong (
  id INT AUTO_INCREMENT PRIMARY KEY,
  giang_vien_id INT NOT NULL,
  hoc_ky_id INT NOT NULL,
  mon_hoc_id INT NOT NULL,
  muc_uu_tien TINYINT NOT NULL DEFAULT 3,
  ca_uu_tien VARCHAR(30),
  trang_thai ENUM('CHO_DUYET','DA_DUYET','TU_CHOI') NOT NULL DEFAULT 'CHO_DUYET',
  UNIQUE (giang_vien_id, hoc_ky_id, mon_hoc_id),
  FOREIGN KEY (giang_vien_id) REFERENCES giang_vien(id),
  FOREIGN KEY (hoc_ky_id) REFERENCES hoc_ky(id),
  FOREIGN KEY (mon_hoc_id) REFERENCES mon_hoc(id)
);

CREATE TABLE lich_ranh_ban (
  giang_vien_id INT NOT NULL,
  hoc_ky_id INT NOT NULL,
  thu TINYINT NOT NULL CHECK (thu BETWEEN 2 AND 7),
  ca TINYINT NOT NULL CHECK (ca BETWEEN 1 AND 4),
  ranh BOOLEAN NOT NULL DEFAULT TRUE,
  PRIMARY KEY (giang_vien_id, hoc_ky_id, thu, ca),
  FOREIGN KEY (giang_vien_id) REFERENCES giang_vien(id),
  FOREIGN KEY (hoc_ky_id) REFERENCES hoc_ky(id)
);

-- 5. CÔNG TÁC KHÁC
CREATE TABLE loai_cong_tac (
  ma VARCHAR(10) PRIMARY KEY,
  ten VARCHAR(100) NOT NULL,
  so_tiet_mac_dinh DECIMAL(6,1) NOT NULL
);

CREATE TABLE cong_tac_khac (
  id INT AUTO_INCREMENT PRIMARY KEY,
  giang_vien_id INT NOT NULL,
  nam_hoc_id INT NOT NULL,
  loai_ma VARCHAR(10) NOT NULL,
  so_tiet DECIMAL(6,1) NOT NULL,
  la_clc BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (giang_vien_id) REFERENCES giang_vien(id),
  FOREIGN KEY (nam_hoc_id) REFERENCES nam_hoc(id),
  FOREIGN KEY (loai_ma) REFERENCES loai_cong_tac(ma)
);

-- 6. QUY TRÌNH DUYỆT + AUDIT
CREATE TABLE ke_hoach_bo_mon (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nam_hoc_id INT NOT NULL,
  bo_mon_id INT NOT NULL,
  trang_thai ENUM('DRAFT','SUBMITTED','APPROVED','RETURNED') NOT NULL DEFAULT 'DRAFT',
  csr DECIMAL(5,2) NOT NULL DEFAULT 0,
  ly_do_tra_ve VARCHAR(500),
  nguoi_nop INT, ngay_nop DATETIME,
  nguoi_duyet INT, ngay_duyet DATETIME,
  UNIQUE (nam_hoc_id, bo_mon_id),
  FOREIGN KEY (nam_hoc_id) REFERENCES nam_hoc(id),
  FOREIGN KEY (bo_mon_id) REFERENCES bo_mon(id)
);

CREATE TABLE audit_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nguoi_dung_id INT, hanh_dong VARCHAR(50), doi_tuong VARCHAR(50), doi_tuong_id INT,
  chi_tiet JSON, thoi_gian DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 7. MẪU 5
CREATE TABLE hoat_dong_ngoai_truong (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nam_hoc_id INT NOT NULL,
  trinh_do VARCHAR(10) NOT NULL DEFAULT 'ĐH',
  khoa VARCHAR(20) NOT NULL,
  noi_dung VARCHAR(200) NOT NULL,
  dia_diem VARCHAR(200),
  hoc_ky TINYINT NOT NULL,
  so_ngay INT, tu_ngay DATE, den_ngay DATE,
  so_luong_sv INT,
  FOREIGN KEY (nam_hoc_id) REFERENCES nam_hoc(id)
);

-- 8. VIEW TÍNH TẢI LƯỢNG
CREATE VIEW v_tai_luong_hk AS
SELECT g.id AS giang_vien_id, h.nam_hoc_id, h.so_hk,
       SUM(n.so_tiet_thuc_te) AS tiet_thuc_te
FROM nhom_hoc_phan n
JOIN ke_hoach_mo_lop k ON k.id = n.ke_hoach_id
JOIN hoc_ky h ON h.id = k.hoc_ky_id
JOIN giang_vien g ON g.id = n.giang_vien_id
GROUP BY g.id, h.nam_hoc_id, h.so_hk;

CREATE VIEW v_tai_luong_nam AS
SELECT g.id AS giang_vien_id, g.ma_cb, g.ho_ten, g.bo_mon_id, g.dinh_muc_min, g.dinh_muc_max,
       h.nam_hoc_id,
       COALESCE(SUM(n.so_tiet_thuc_te),0) AS tiet_giang_day,
       COALESCE((SELECT SUM(c.so_tiet) FROM cong_tac_khac c
                 WHERE c.giang_vien_id = g.id AND c.nam_hoc_id = h.nam_hoc_id),0) AS tiet_cong_tac_khac
FROM giang_vien g
JOIN hoc_ky h
LEFT JOIN ke_hoach_mo_lop k ON k.hoc_ky_id = h.id
LEFT JOIN nhom_hoc_phan n ON n.ke_hoach_id = k.id AND n.giang_vien_id = g.id
WHERE g.loai = 'CO_HUU' AND h.so_hk = 1
GROUP BY g.id, g.ma_cb, g.ho_ten, g.bo_mon_id, g.dinh_muc_min, g.dinh_muc_max, h.nam_hoc_id;

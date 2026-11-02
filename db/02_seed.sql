USE dacn_phancong;

INSERT INTO BoMon (MaBoMon, TenBoMon) VALUES
  ('BM_A', 'Bộ môn A (demo)'),
  ('BM_B', 'Bộ môn B (demo)');

INSERT INTO GiangVien (MaGV, HoTen, HocHamHocVi, Status, HeSoLuong, MaBoMon, SemesterCap, SemesterTarget) VALUES
  ('GV01', 'Giảng viên 01', 'ThS', 'CHINH_THUC', 3.00, 'BM_A', 180, 150),
  ('GV02', 'Giảng viên 02', 'ThS', 'CHINH_THUC', 3.00, 'BM_A', 180, 150),
  ('GV03', 'Giảng viên 03', 'TS',  'CHINH_THUC', 3.50, 'BM_A', 180, 150),
  ('GV04', 'Giảng viên 04', 'TS',  'CHINH_THUC', 3.50, 'BM_B', 180, 150),
  ('GV05', 'Giảng viên 05', 'ThS', 'CHINH_THUC', 3.00, 'BM_B', 180, 150),
  ('GV06', 'Giảng viên 06', 'ThS', 'CHINH_THUC', 3.00, 'BM_B', 180, 150);

UPDATE BoMon SET MaTruongBoMon = 'GV01' WHERE MaBoMon = 'BM_A';
UPDATE BoMon SET MaTruongBoMon = 'GV04' WHERE MaBoMon = 'BM_B';

INSERT INTO HocKy (MaHocKy, TenHocKy, NamHoc, RegistrationOpen, Revision) VALUES
  ('HK1_2627', 'Học kỳ 1', '2026-2027', 0, 0);

INSERT INTO KeHoachBoMon (MaHocKy, MaBoMon) VALUES
  ('HK1_2627', 'BM_A'),
  ('HK1_2627', 'BM_B');

-- Tài khoản tạm để thử đăng nhập; danh sách chính thức bổ sung sau
INSERT INTO TaiKhoan (TenDangNhap, MatKhauHash, VaiTro, MaGV) VALUES
  ('tldt01', '{noop}123456', 'TLDT', NULL),
  ('bcn01',  '{noop}123456', 'BCN',  NULL),
  ('tbm_a',  '{noop}123456', 'TBM',  'GV01'),
  ('tbm_b',  '{noop}123456', 'TBM',  'GV04'),
  ('gv02',   '{noop}123456', 'GV',   'GV02'),
  ('gv03',   '{noop}123456', 'GV',   'GV03'),
  ('gv05',   '{noop}123456', 'GV',   'GV05');
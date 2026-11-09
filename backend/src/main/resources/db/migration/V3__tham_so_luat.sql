CREATE TABLE tham_so_luat (
  ma VARCHAR(30) PRIMARY KEY,
  mo_ta VARCHAR(200) NOT NULL,
  gia_tri DECIMAL(10,2) NOT NULL,
  don_vi VARCHAR(30),
  ngay_hieu_luc DATE,
  nguon_phap_ly VARCHAR(100)
);

INSERT INTO tham_so_luat(ma, mo_ta, gia_tri, don_vi, ngay_hieu_luc, nguon_phap_ly) VALUES
  ('DINH_MUC_MIN',      'Định mức tối thiểu GV chính thức',  200, 'giờ chuẩn', '2020-07-27', 'TT20/2020'),
  ('DINH_MUC_MAX',      'Định mức tối đa (trần)',            350, 'giờ chuẩn', '2020-07-27', 'TT20/2020'),
  ('DINH_MUC_TAP_SU',   'Định mức GV tập sự',               175, 'giờ chuẩn', '2020-07-27', 'TT20/2020'),
  ('TY_LE_GIANG_DAY',   'Tỷ lệ giảng dạy trực tiếp tối thiểu', 50, '%',      '2020-07-27', 'TT20/2020'),
  ('NCKH_GIO',          'Định mức NCKH',                     586, 'giờ',       '2020-07-27', 'TT20/2020'),
  ('HE_SO_LOP_DONG_MIN','Hệ số lớp đông tối thiểu',         1.0, 'hệ số',     NULL,          'Quy định Khoa'),
  ('HE_SO_LOP_DONG_MAX','Hệ số lớp đông tối đa',            1.5, 'hệ số',     NULL,          'Quy định Khoa'),
  ('MAX_SV_NHOM',       'Sĩ số tối đa / nhóm',               50, 'SV',         NULL,          'Quy định Khoa');

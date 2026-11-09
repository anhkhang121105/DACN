-- =====================================================================
-- MOCK DATA (dữ liệu giả, không phải người thật)
-- =====================================================================
INSERT INTO nam_hoc(ten) VALUES ('2025-2026');
INSERT INTO hoc_ky(nam_hoc_id,so_hk,ngay_bd,ngay_kt,mo_dang_ky) VALUES
 (1,1,'2025-09-08','2026-01-17',FALSE),
 (1,2,'2026-02-02','2026-06-06',TRUE),
 (1,3,'2026-06-22','2026-08-15',FALSE);

INSERT INTO bo_mon(ma,ten) VALUES
 ('KTPM','Kỹ thuật phần mềm'),
 ('HTTT','Hệ thống thông tin'),
 ('MMT','Mạng máy tính & An toàn thông tin');

INSERT INTO giang_vien(ma_cb,ho_ten,nam_sinh,hoc_vi,chuc_danh,loai,bo_mon_id,don_vi_cong_tac,chuyen_nganh,la_tap_su) VALUES
 ('10001','Nguyễn Văn An',1975,'TS','GVC','CO_HUU',1,'Khoa CNTT',NULL,FALSE),
 ('10002','Trần Thị Bình',1982,'ThS','GV','CO_HUU',1,'Khoa CNTT',NULL,FALSE),
 ('10003','Lê Minh Châu',1990,'ThS','GV','CO_HUU',1,'Khoa CNTT',NULL,FALSE),
 ('10004','Phạm Quốc Dũng',1972,'PGS.TS','GVCC','CO_HUU',2,'Khoa CNTT',NULL,FALSE),
 ('10005','Võ Thị Hà',1985,'ThS','GV','CO_HUU',2,'Khoa CNTT',NULL,FALSE),
 ('10006','Đặng Hoàng Khoa',1998,'ThS','GV','CO_HUU',3,'Khoa CNTT',NULL,TRUE),
 ('10007','Bùi Thanh Lâm',1980,'TS','GV','CO_HUU',3,'Khoa CNTT',NULL,FALSE),
 ('10008','Hồ Ngọc Mai',1988,'ThS','GV','CO_HUU',3,'Khoa CNTT',NULL,FALSE),
 ('20001','Ngô Văn Nam',1979,'ThS','GV','MOI_GIANG',NULL,'Phòng Khảo thí & ĐBCL',NULL,FALSE),
 ('20002','Đinh Thị Oanh',1976,'TS','GVC','MOI_GIANG',NULL,'Khoa Toán - Ứng dụng',NULL,FALSE),
 ('30001','Lý Minh Phúc',1983,'TS','GV','THINH_GIANG',NULL,'ĐH Sư phạm TP.HCM','CNTT',FALSE),
 ('30002','Trương Gia Quý',1970,'ThS','GV','THINH_GIANG',NULL,'ĐH Bách Khoa TP.HCM','Khoa học máy tính',FALSE);
UPDATE giang_vien SET dinh_muc_min = 175 WHERE la_tap_su;

-- Mật khẩu tạm '123456'; DataInitializer sẽ re-hash các hash chứa 'MOCK' khi khởi động
INSERT INTO nguoi_dung(username,password_hash,vai_tro,giang_vien_id) VALUES
 ('trolydt','$2a$10$MOCKHASHxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','TRO_LY',NULL),
 ('bcnkhoa','$2a$10$MOCKHASHxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','BCN_KHOA',4),
 ('tbm_ktpm','$2a$10$MOCKHASHxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','TRUONG_BO_MON',1),
 ('tbm_httt','$2a$10$MOCKHASHxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','TRUONG_BO_MON',4),
 ('tbm_mmt','$2a$10$MOCKHASHxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','TRUONG_BO_MON',7),
 ('gv10002','$2a$10$MOCKHASHxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx','GIANG_VIEN',2);

INSERT INTO chuong_trinh_dao_tao(ma,ten,he,khoa_tuyen) VALUES
 ('CNTT25','Công nghệ thông tin - K25','DAI_TRA','25'),
 ('CNTT25CLC','Công nghệ thông tin CLC - K25','CLC','25');
INSERT INTO khoi_kien_thuc(ten) VALUES ('Cơ sở ngành'),('Chuyên ngành'),('Đại cương');

INSERT INTO mon_hoc(ma_hp,ten,so_tc,tiet_lt,tiet_bt,tiet_th,he_so_hp,bo_mon_id,khoi_kien_thuc_id) VALUES
 ('841302','Cơ sở lập trình',4,45,0,30,0.80,1,1),
 ('841303','Kỹ thuật lập trình',4,45,0,30,0.80,1,1),
 ('841022','Hệ điều hành',3,30,0,30,0.75,3,1),
 ('841108','Cấu trúc dữ liệu và giải thuật',4,45,0,30,0.80,2,1),
 ('841404','Mạng máy tính',3,30,0,30,0.75,3,2),
 ('841044','Phương pháp lập trình hướng đối tượng',4,45,0,30,0.80,1,2),
 ('841021','Kiến trúc máy tính',3,30,0,30,0.75,3,1),
 ('841403','Cấu trúc rời rạc',4,60,0,0,1.00,2,3);

INSERT INTO ctdt_mon_hoc(ctdt_id,mon_hoc_id,hk_du_kien)
SELECT 1, id, 1 FROM mon_hoc;

INSERT INTO giang_vien_mon_hoc(giang_vien_id,mon_hoc_id) VALUES
 (1,1),(1,2),(1,6),(2,1),(2,2),(2,6),(3,1),(3,2),(3,6),
 (4,4),(4,8),(5,4),(5,8),
 (6,3),(6,5),(6,7),(7,3),(7,5),(7,7),(8,3),(8,5),(8,7),
 (9,1),(9,2),(11,6),(12,7);

INSERT INTO ke_hoach_mo_lop(hoc_ky_id,mon_hoc_id,khoa_hien_thi,tong_sv,max_sv_nhom,ghi_chu) VALUES
 (1,1,'25 CNTT + KTPM + TTNT',450,50,NULL),
 (2,2,'25 CNTT + KTPM + TTNT',450,50,NULL),
 (3,3,'25 CNTT + KTPM + TTNT',480,50,NULL),
 (1,4,'24 CNTT',150,50,NULL),
 (2,5,'24 CNTT',100,50,NULL);

INSERT INTO nhom_hoc_phan(ke_hoach_id,so_nhom,loai,so_tiet_thuc_hien,he_so,giang_vien_id,nguon,is_fixed)
SELECT 1, n, 'LT_TH', 75, 0.80,
  CASE n WHEN 1 THEN 1 WHEN 2 THEN 1 WHEN 3 THEN 2 WHEN 4 THEN 2 ELSE NULL END,
  CASE WHEN n<=4 THEN 'THU_CONG' ELSE 'CHUA' END,
  n<=4
FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
      UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t;

INSERT INTO nhom_hoc_phan(ke_hoach_id,so_nhom,loai,so_tiet_thuc_hien,he_so)
SELECT 2, n, 'LT_TH', 75, 0.80
FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
      UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) t;

INSERT INTO nhom_hoc_phan(ke_hoach_id,so_nhom,loai,so_tiet_thuc_hien,he_so,giang_vien_id,nguon,is_fixed)
SELECT 3, n, 'LT_TH', 60, 0.75,
  CASE WHEN n<=3 THEN 6 WHEN n<=6 THEN 7 WHEN n<=8 THEN 8 ELSE NULL END,
  CASE WHEN n<=8 THEN 'THU_CONG' ELSE 'CHUA' END, n<=8
FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
      UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10) t;

UPDATE nhom_hoc_phan SET giang_vien_id = 9, nguon='THU_CONG', is_fixed=TRUE
WHERE ke_hoach_id=1 AND so_nhom IN (5,6);

INSERT INTO nhom_hoc_phan(ke_hoach_id,so_nhom,loai,so_tiet_thuc_hien,he_so,giang_vien_id,nguon,is_fixed) VALUES
 (4,1,'LT_TH',75,0.80,11,'THU_CONG',TRUE),
 (4,2,'LT_TH',75,0.80,11,'THU_CONG',TRUE),
 (4,3,'LT_TH',75,0.80,NULL,'CHUA',FALSE);

INSERT INTO nguyen_vong(giang_vien_id,hoc_ky_id,mon_hoc_id,muc_uu_tien,trang_thai) VALUES
 (1,2,2,5,'DA_DUYET'),(2,2,2,4,'DA_DUYET'),(3,2,2,3,'CHO_DUYET'),
 (6,3,3,5,'DA_DUYET'),(7,3,3,4,'DA_DUYET'),(8,3,3,3,'DA_DUYET');

INSERT INTO lich_ranh_ban(giang_vien_id,hoc_ky_id,thu,ca,ranh)
SELECT 3, 2, t.thu, c.ca, NOT (t.thu=7 AND c.ca=1)
FROM (SELECT 2 thu UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7) t
CROSS JOIN (SELECT 1 ca UNION SELECT 2 UNION SELECT 3) c;

INSERT INTO loai_cong_tac VALUES
 ('TK','Thư ký Khoa/Bộ môn',140),('PBT','Phó bí thư',35),
 ('CVHT','Cố vấn học tập',20),('NCKH','Nghiên cứu khoa học',60);
INSERT INTO cong_tac_khac(giang_vien_id,nam_hoc_id,loai_ma,so_tiet,la_clc) VALUES
 (1,1,'TK',140,FALSE),(1,1,'PBT',35,FALSE),(2,1,'CVHT',20,TRUE),(3,1,'NCKH',60,FALSE);

INSERT INTO ke_hoach_bo_mon(nam_hoc_id,bo_mon_id,trang_thai,csr) VALUES
 (1,1,'DRAFT',62.50),(1,2,'DRAFT',0),(1,3,'SUBMITTED',100);

INSERT INTO hoat_dong_ngoai_truong(nam_hoc_id,khoa,noi_dung,hoc_ky,so_ngay,tu_ngay,den_ngay,so_luong_sv) VALUES
 (1,'21+22','Thực tập tốt nghiệp (HK1)',1,54,'2025-09-29','2025-11-22',300),
 (1,'22+23','Thực tập tốt nghiệp (HK2)',2,54,'2026-03-09','2026-05-02',300),
 (1,'22+23','Thực tập tốt nghiệp (HK3)',3,54,'2026-06-22','2026-08-15',300);

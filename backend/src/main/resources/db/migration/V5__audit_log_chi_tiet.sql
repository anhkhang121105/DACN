ALTER TABLE audit_log
  ADD COLUMN nguoi_thuc_hien INT NULL,
  ADD COLUMN ten_bang_tac_dong VARCHAR(100) NULL,
  ADD COLUMN ban_ghi_id INT NULL,
  ADD COLUMN gia_tri_cu JSON NULL,
  ADD COLUMN gia_tri_moi JSON NULL;

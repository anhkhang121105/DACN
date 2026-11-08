-- Bổ sung thời khóa biểu tối thiểu cho nhóm học phần để kiểm tra trùng ca / lịch rảnh.
-- (Schema gốc chỉ có số tiết; các luật KHONG_TRUNG_CA và GV_RANH cần biết nhóm diễn ra thứ-ca nào.)
ALTER TABLE nhom_hoc_phan
  ADD COLUMN thu TINYINT NULL,
  ADD COLUMN ca TINYINT NULL;

ALTER TABLE nhom_hoc_phan
  ADD CONSTRAINT chk_nhom_thu CHECK (thu IS NULL OR thu BETWEEN 2 AND 7),
  ADD CONSTRAINT chk_nhom_ca CHECK (ca IS NULL OR ca BETWEEN 1 AND 4);

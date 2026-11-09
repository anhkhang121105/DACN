package vn.edu.sgu.phancong.rules.impl;

import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.BusinessRule;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleViolation;

@Component
public class DungChuyenMonRule implements BusinessRule {

    @Override
    public String ma() {
        return "DUNG_CHUYEN_MON";
    }

    @Override
    public String moTa() {
        return "Giảng viên phải đúng chuyên môn môn học";
    }

    @Override
    public boolean isCung() {
        return true;
    }

    @Override
    public RuleViolation check(RuleContext context, NhomHocPhan nhom, GiangVien gv) {
        if (gv == null) {
            return violation(nhom, null, "chưa chọn giảng viên");
        }
        Integer monHocId = nhom.getKeHoach().getMonHoc().getId();
        if (!context.canTeach(gv.getId(), monHocId, nhom.getLoai())) {
            return violation(nhom, gv, gv.getHoTen() + " không dạy được môn "
                    + nhom.getKeHoach().getMonHoc().getTen() + " (" + nhom.getLoai() + ")");
        }
        return null;
    }
}

package vn.edu.sgu.phancong.rules.impl;

import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.BusinessRule;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleViolation;

@Component
public class KhongTrungCaRule implements BusinessRule {

    @Override
    public String ma() {
        return "KHONG_TRUNG_CA";
    }

    @Override
    public String moTa() {
        return "Giảng viên không dạy 2 nhóm cùng thứ-ca";
    }

    @Override
    public boolean isCung() {
        return true;
    }

    @Override
    public RuleViolation check(RuleContext context, NhomHocPhan nhom, GiangVien gv) {
        if (gv == null || nhom.getThu() == null || nhom.getCa() == null) {
            return null;
        }
        Integer hocKyId = nhom.getKeHoach().getHocKy().getId();
        Integer owner = context.slotOwner(gv.getId(), hocKyId, nhom.getThu(), nhom.getCa());
        if (owner != null && !owner.equals(nhom.getId())) {
            return violation(nhom, gv, "trùng ca thứ " + nhom.getThu() + " ca " + nhom.getCa());
        }
        return null;
    }
}

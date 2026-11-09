package vn.edu.sgu.phancong.rules.impl;

import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.BusinessRule;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleViolation;

@Component
public class GvRanhRule implements BusinessRule {

    @Override
    public String ma() {
        return "GV_RANH";
    }

    @Override
    public String moTa() {
        return "Giảng viên phải rảnh ở ca nhóm diễn ra";
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
        if (context.isBusy(gv.getId(), hocKyId, nhom.getThu(), nhom.getCa())) {
            return violation(nhom, gv, "bận thứ " + nhom.getThu() + " ca " + nhom.getCa());
        }
        return null;
    }
}

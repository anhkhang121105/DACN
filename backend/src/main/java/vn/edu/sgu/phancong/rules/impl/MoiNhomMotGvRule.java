package vn.edu.sgu.phancong.rules.impl;

import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.BusinessRule;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleViolation;

@Component
public class MoiNhomMotGvRule implements BusinessRule {

    @Override
    public String ma() {
        return "MOI_NHOM_MOT_GV";
    }

    @Override
    public String moTa() {
        return "Mỗi nhóm học phần phải có đúng một giảng viên";
    }

    @Override
    public boolean isCung() {
        return true;
    }

    @Override
    public RuleViolation check(RuleContext context, NhomHocPhan nhom, GiangVien gv) {
        if (gv == null) {
            return violation(nhom, null, "nhóm chưa được phân công");
        }
        return null;
    }
}

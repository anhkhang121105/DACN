package vn.edu.sgu.phancong.rules.impl;

import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.BusinessRule;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleViolation;

import java.math.BigDecimal;

@Component
public class KhongVuotTranRule implements BusinessRule {

    @Override
    public String ma() {
        return "KHONG_VUOT_TRAN";
    }

    @Override
    public String moTa() {
        return "Tổng tiết thực tế không vượt định mức tối đa";
    }

    @Override
    public boolean isCung() {
        return true;
    }

    @Override
    public RuleViolation check(RuleContext context, NhomHocPhan nhom, GiangVien gv) {
        if (gv == null) {
            return null;
        }
        BigDecimal tiet = nhom.tinhTietThucTe();
        BigDecimal hienTai = context.workload(gv.getId());
        Integer assignee = context.assigneeOf(nhom.getId());
        if (assignee != null && assignee.equals(gv.getId())) {
            hienTai = hienTai.subtract(tiet);
        }
        BigDecimal sauKhiGan = hienTai.add(tiet);
        BigDecimal tran = gv.getDinhMucMax() != null ? gv.getDinhMucMax() : BigDecimal.valueOf(350);
        if (sauKhiGan.compareTo(tran) > 0) {
            return violation(nhom, gv, "tải " + sauKhiGan + " > trần " + tran);
        }
        return null;
    }
}

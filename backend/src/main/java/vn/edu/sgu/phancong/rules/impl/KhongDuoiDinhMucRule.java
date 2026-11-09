package vn.edu.sgu.phancong.rules.impl;

import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.BusinessRule;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleViolation;

import java.math.BigDecimal;

@Component
public class KhongDuoiDinhMucRule implements BusinessRule {

    @Override
    public String ma() {
        return "KHONG_DUOI_DINH_MUC";
    }

    @Override
    public String moTa() {
        return "Tổng tiết nên đạt định mức tối thiểu";
    }

    @Override
    public boolean isCung() {
        return false;
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
        BigDecimal min = gv.getDinhMucMin() != null ? gv.getDinhMucMin() : BigDecimal.valueOf(200);
        if (sauKhiGan.compareTo(min) < 0) {
            return violation(nhom, gv, "tải " + sauKhiGan + " < định mức " + min + " (cảnh báo)");
        }
        return null;
    }
}

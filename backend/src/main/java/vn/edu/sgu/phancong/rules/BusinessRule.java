package vn.edu.sgu.phancong.rules;

import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;

public interface BusinessRule {

    String ma();

    String moTa();

    boolean isCung();

    RuleViolation check(RuleContext context, NhomHocPhan nhom, GiangVien giangVien);

    default RuleViolation violation(NhomHocPhan nhom, GiangVien gv, String chiTiet) {
        return RuleViolation.builder()
                .maLuat(ma())
                .moTa(moTa() + (chiTiet != null ? ": " + chiTiet : ""))
                .cung(isCung())
                .giangVienId(gv != null ? gv.getId() : null)
                .nhomId(nhom != null ? nhom.getId() : null)
                .build();
    }
}

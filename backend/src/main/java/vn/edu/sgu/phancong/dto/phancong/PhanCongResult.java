package vn.edu.sgu.phancong.dto.phancong;

import lombok.Builder;
import lombok.Getter;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.RuleViolation;

import java.util.List;

@Getter
@Builder
public class PhanCongResult {
    private NhomHocPhan nhom;
    private List<RuleViolation> violations;
    private boolean thanhCong;
}

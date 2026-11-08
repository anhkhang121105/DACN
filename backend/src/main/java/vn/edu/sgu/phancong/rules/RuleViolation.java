package vn.edu.sgu.phancong.rules;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RuleViolation {
    private String maLuat;
    private String moTa;
    private boolean cung;
    private Integer giangVienId;
    private Integer nhomId;
}

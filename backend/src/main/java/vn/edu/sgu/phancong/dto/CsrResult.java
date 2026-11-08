package vn.edu.sgu.phancong.dto;

import lombok.Builder;
import lombok.Getter;
import vn.edu.sgu.phancong.rules.RuleViolation;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class CsrResult {
    private BigDecimal csr;
    private int totalChecks;
    private int satisfiedChecks;
    private int hardViolationCount;
    private List<RuleViolation> violations;
}

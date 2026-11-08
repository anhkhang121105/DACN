package vn.edu.sgu.phancong.rules;

import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.dto.CsrResult;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.impl.MoiNhomMotGvRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class RulesEngine {

    private final List<BusinessRule> rules;
    private final MoiNhomMotGvRule moiNhomMotGvRule;

    public RulesEngine(List<BusinessRule> rules, MoiNhomMotGvRule moiNhomMotGvRule) {
        this.rules = rules;
        this.moiNhomMotGvRule = moiNhomMotGvRule;
    }

    public List<BusinessRule> allRules() {
        return rules;
    }

    public List<RuleViolation> evaluateAssignment(RuleContext context, NhomHocPhan nhom, GiangVien gv) {
        List<RuleViolation> violations = new ArrayList<>();
        for (BusinessRule rule : rules) {
            RuleViolation v = rule.check(context, nhom, gv);
            if (v != null) {
                violations.add(v);
            }
        }
        return violations;
    }

    public List<RuleViolation> evaluateNhom(RuleContext context, NhomHocPhan nhom) {
        if (nhom.getGiangVien() == null) {
            List<RuleViolation> violations = new ArrayList<>();
            RuleViolation v = moiNhomMotGvRule.check(context, nhom, null);
            if (v != null) {
                violations.add(v);
            }
            return violations;
        }
        return evaluateAssignment(context, nhom, nhom.getGiangVien());
    }

    public CsrResult computeCsr(RuleContext context, List<NhomHocPhan> nhoms) {
        int totalChecks = 0;
        int satisfied = 0;
        List<RuleViolation> allViolations = new ArrayList<>();
        long hardRuleCount = rules.stream().filter(BusinessRule::isCung).count();

        for (NhomHocPhan nhom : nhoms) {
            if (nhom.getGiangVien() == null) {
                totalChecks += 1;
                RuleViolation v = moiNhomMotGvRule.check(context, nhom, null);
                if (v != null) {
                    allViolations.add(v);
                }
                continue;
            }
            totalChecks += (int) hardRuleCount;
            List<RuleViolation> violations = evaluateAssignment(context, nhom, nhom.getGiangVien());
            int hardViolations = 0;
            for (RuleViolation v : violations) {
                allViolations.add(v);
                if (v.isCung()) {
                    hardViolations++;
                }
            }
            satisfied += (int) hardRuleCount - hardViolations;
        }

        BigDecimal csr = totalChecks == 0
                ? BigDecimal.valueOf(100)
                : BigDecimal.valueOf(satisfied)
                        .multiply(BigDecimal.valueOf(100))
                        .divide(BigDecimal.valueOf(totalChecks), 2, RoundingMode.HALF_UP);

        return CsrResult.builder()
                .csr(csr)
                .totalChecks(totalChecks)
                .satisfiedChecks(satisfied)
                .hardViolationCount(allViolations.stream().filter(RuleViolation::isCung).toList().size())
                .violations(allViolations)
                .build();
    }
}

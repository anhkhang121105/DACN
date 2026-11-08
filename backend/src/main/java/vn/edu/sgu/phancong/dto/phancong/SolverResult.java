package vn.edu.sgu.phancong.dto.phancong;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SolverResult {
    private String status;
    private String message;
    private double objectiveValue;
    private List<SolverAssignment> assignments;
}

package vn.edu.sgu.phancong.dto.phancong;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class SolverAssignment {
    private Integer nhomId;
    private Integer soNhom;
    private String loai;
    private String monHoc;
    private BigDecimal soTietThucTe;
    private Integer giangVienId;
    private String giangVienTen;
    private int diemUuTien;
}

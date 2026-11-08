package vn.edu.sgu.phancong.dto.report;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TaiLuongDto {
    private Integer giangVienId;
    private String maCb;
    private String hoTen;
    private String hocVi;
    private Integer boMonId;
    private String boMonTen;
    private BigDecimal tietGiangDay;
    private BigDecimal tietCongTacKhac;
    private BigDecimal tongTiet;
    private BigDecimal dinhMucMin;
    private BigDecimal dinhMucMax;
    private BigDecimal phanTramTai;
    private String trangThai;
}

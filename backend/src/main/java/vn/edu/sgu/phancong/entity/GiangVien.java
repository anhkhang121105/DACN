package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.enums.LoaiGiangVien;

import java.math.BigDecimal;

@Entity
@Table(name = "giang_vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiangVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_cb", nullable = false, unique = true, length = 10)
    private String maCb;

    @Column(name = "ho_ten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "nam_sinh")
    private Integer namSinh;

    @Column(name = "hoc_vi", length = 20)
    private String hocVi;

    @Column(name = "chuc_danh", length = 20)
    private String chucDanh;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoaiGiangVien loai;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bo_mon_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private BoMon boMon;

    @Column(name = "don_vi_cong_tac", length = 150)
    private String donViCongTac;

    @Column(name = "chuyen_nganh", length = 100)
    private String chuyenNganh;

    @Column(name = "la_tap_su", nullable = false)
    private Boolean laTapSu = false;

    @Column(name = "dinh_muc_min", nullable = false, precision = 6, scale = 1)
    private BigDecimal dinhMucMin;

    @Column(name = "dinh_muc_max", nullable = false, precision = 6, scale = 1)
    private BigDecimal dinhMucMax;
}

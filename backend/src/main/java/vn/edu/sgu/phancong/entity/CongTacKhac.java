package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cong_tac_khac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CongTacKhac {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private GiangVien giangVien;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nam_hoc_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private NamHoc namHoc;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "loai_ma", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private LoaiCongTac loaiCongTac;

    @Column(name = "so_tiet", nullable = false, precision = 6, scale = 1)
    private BigDecimal soTiet;

    @Column(name = "la_clc", nullable = false)
    private Boolean laClc = false;
}

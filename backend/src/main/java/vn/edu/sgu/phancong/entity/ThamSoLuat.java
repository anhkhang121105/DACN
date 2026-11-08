package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tham_so_luat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ThamSoLuat {

    @Id
    @Column(length = 30)
    private String ma;

    @Column(name = "mo_ta", nullable = false, length = 200)
    private String moTa;

    @Column(name = "gia_tri", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "don_vi", length = 30)
    private String donVi;

    @Column(name = "ngay_hieu_luc")
    private LocalDate ngayHieuLuc;

    @Column(name = "nguon_phap_ly", length = 100)
    private String nguonPhapLy;
}

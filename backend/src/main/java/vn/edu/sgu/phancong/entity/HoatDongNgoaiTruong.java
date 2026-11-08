package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hoat_dong_ngoai_truong")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoatDongNgoaiTruong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nam_hoc_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private NamHoc namHoc;

    @Column(name = "trinh_do", nullable = false, length = 10)
    private String trinhDo = "ĐH";

    @Column(nullable = false, length = 20)
    private String khoa;

    @Column(name = "noi_dung", nullable = false, length = 200)
    private String noiDung;

    @Column(name = "dia_diem", length = 200)
    private String diaDiem;

    @Column(name = "hoc_ky", nullable = false)
    private Integer hocKy;

    @Column(name = "so_ngay")
    private Integer soNgay;

    @Column(name = "tu_ngay")
    private LocalDate tuNgay;

    @Column(name = "den_ngay")
    private LocalDate denNgay;

    @Column(name = "so_luong_sv")
    private Integer soLuongSv;
}

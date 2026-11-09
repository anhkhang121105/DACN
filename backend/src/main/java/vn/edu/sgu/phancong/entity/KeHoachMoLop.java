package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ke_hoach_mo_lop",
        uniqueConstraints = @UniqueConstraint(columnNames = {"hoc_ky_id", "mon_hoc_id", "khoa_hien_thi"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeHoachMoLop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hoc_ky_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "namHoc"})
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mon_hoc_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MonHoc monHoc;

    @Column(name = "khoa_hien_thi", length = 100)
    private String khoaHienThi;

    @Column(name = "tong_sv", nullable = false)
    private Integer tongSv;

    @Column(name = "max_sv_nhom", nullable = false)
    private Integer maxSvNhom = 50;

    @Column(name = "so_nhom", insertable = false, updatable = false)
    private Integer soNhom;

    @Column(length = 500)
    private String ghiChu;

    @Transient
    public int tinhSoNhom() {
        if (maxSvNhom == null || maxSvNhom <= 0 || tongSv == null) {
            return 0;
        }
        return (int) Math.ceil((double) tongSv / maxSvNhom);
    }
}

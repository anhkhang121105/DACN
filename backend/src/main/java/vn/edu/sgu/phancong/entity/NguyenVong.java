package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNguyenVong;

@Entity
@Table(name = "nguyen_vong",
        uniqueConstraints = @UniqueConstraint(columnNames = {"giang_vien_id", "hoc_ky_id", "mon_hoc_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguyenVong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "giang_vien_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private GiangVien giangVien;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "hoc_ky_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "mon_hoc_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private MonHoc monHoc;

    @Column(name = "muc_uu_tien", nullable = false)
    private Integer mucUuTien = 3;

    @Column(name = "ca_uu_tien", length = 30)
    private String caUuTien;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiNguyenVong trangThai = TrangThaiNguyenVong.CHO_DUYET;
}

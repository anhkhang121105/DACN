package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.enums.LoaiNhom;
import vn.edu.sgu.phancong.entity.enums.NguonPhanCong;

import java.math.BigDecimal;

@Entity
@Table(name = "nhom_hoc_phan",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ke_hoach_id", "so_nhom", "loai"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhomHocPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "ke_hoach_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private KeHoachMoLop keHoach;

    @Column(name = "so_nhom", nullable = false)
    private Integer soNhom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private LoaiNhom loai;

    @Column(name = "so_tiet_thuc_hien", nullable = false, precision = 6, scale = 1)
    private BigDecimal soTietThucHien;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal heSo;

    @Column(name = "so_tiet_thuc_te", insertable = false, updatable = false, precision = 7, scale = 2)
    private BigDecimal soTietThucTe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "giang_vien_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private GiangVien giangVien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NguonPhanCong nguon = NguonPhanCong.CHUA;

    @Column(name = "is_fixed", nullable = false)
    private Boolean isFixed = false;

    @Column(name = "thu")
    private Integer thu;

    @Column(name = "ca")
    private Integer ca;

    @Transient
    public BigDecimal tinhTietThucTe() {
        if (soTietThucHien == null || heSo == null) {
            return BigDecimal.ZERO;
        }
        return soTietThucHien.multiply(heSo).setScale(2, java.math.RoundingMode.HALF_UP);
    }
}

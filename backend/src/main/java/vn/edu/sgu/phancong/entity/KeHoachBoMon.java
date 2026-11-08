package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.enums.TrangThaiKeHoach;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ke_hoach_bo_mon",
        uniqueConstraints = @UniqueConstraint(columnNames = {"nam_hoc_id", "bo_mon_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeHoachBoMon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nam_hoc_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private NamHoc namHoc;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bo_mon_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private BoMon boMon;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiKeHoach trangThai = TrangThaiKeHoach.DRAFT;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal csr = BigDecimal.ZERO;

    @Column(name = "ly_do_tra_ve", length = 500)
    private String lyDoTraVe;

    @Column(name = "nguoi_nop")
    private Integer nguoiNop;

    @Column(name = "ngay_nop")
    private LocalDateTime ngayNop;

    @Column(name = "nguoi_duyet")
    private Integer nguoiDuyet;

    @Column(name = "ngay_duyet")
    private LocalDateTime ngayDuyet;
}

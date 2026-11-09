package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "mon_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_hp", nullable = false, unique = true, length = 10)
    private String maHp;

    @Column(nullable = false, length = 150)
    private String ten;

    @Column(name = "so_tc", nullable = false)
    private Integer soTc;

    @Column(name = "tiet_lt", nullable = false)
    private Integer tietLt = 0;

    @Column(name = "tiet_bt", nullable = false)
    private Integer tietBt = 0;

    @Column(name = "tiet_th", nullable = false)
    private Integer tietTh = 0;

    @Column(name = "tiet_tc", insertable = false, updatable = false)
    private Integer tietTc;

    @Column(name = "he_so_hp", nullable = false, precision = 4, scale = 2)
    private BigDecimal heSoHp = BigDecimal.ONE;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "bo_mon_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private BoMon boMon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "khoi_kien_thuc_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private KhoiKienThuc khoiKienThuc;

    public int getTongTiet() {
        return (tietLt == null ? 0 : tietLt) + (tietBt == null ? 0 : tietBt) + (tietTh == null ? 0 : tietTh);
    }
}

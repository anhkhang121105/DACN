package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hoc_ky", uniqueConstraints = @UniqueConstraint(columnNames = {"nam_hoc_id", "so_hk"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HocKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "nam_hoc_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private NamHoc namHoc;

    @Column(name = "so_hk", nullable = false)
    private Integer soHk;

    @Column(name = "ngay_bd")
    private LocalDate ngayBd;

    @Column(name = "ngay_kt")
    private LocalDate ngayKt;

    @Column(name = "mo_dang_ky", nullable = false)
    private Boolean moDangKy = false;
}

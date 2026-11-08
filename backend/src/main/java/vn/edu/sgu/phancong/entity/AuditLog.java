package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nguoi_dung_id")
    private Integer nguoiDungId;

    @Column(name = "hanh_dong", length = 50)
    private String hanhDong;

    @Column(name = "doi_tuong", length = 50)
    private String doiTuong;

    @Column(name = "doi_tuong_id")
    private Integer doiTuongId;

    @Column(name = "chi_tiet", columnDefinition = "JSON")
    private String chiTiet;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian;

    @PrePersist
    public void prePersist() {
        if (thoiGian == null) {
            thoiGian = LocalDateTime.now();
        }
    }
}

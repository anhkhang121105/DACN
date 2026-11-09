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

    @Column(name = "nguoi_thuc_hien")
    private Integer nguoiThucHien;

    @Column(name = "hanh_dong", length = 50)
    private String hanhDong;

    @Column(name = "doi_tuong", length = 50)
    private String doiTuong;

    @Column(name = "ten_bang_tac_dong", length = 100)
    private String tenBangTacDong;

    @Column(name = "doi_tuong_id")
    private Integer doiTuongId;

    @Column(name = "ban_ghi_id")
    private Integer banGhiId;

    @Column(name = "gia_tri_cu", columnDefinition = "JSON")
    private String giaTriCu;

    @Column(name = "gia_tri_moi", columnDefinition = "JSON")
    private String giaTriMoi;

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

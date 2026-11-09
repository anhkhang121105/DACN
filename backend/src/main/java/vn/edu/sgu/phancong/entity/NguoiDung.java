package vn.edu.sgu.phancong.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.enums.VaiTro;

@Entity
@Table(name = "nguoi_dung")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NguoiDung {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "vai_tro", nullable = false, length = 20)
    private VaiTro vaiTro;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "giang_vien_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private GiangVien giangVien;
}

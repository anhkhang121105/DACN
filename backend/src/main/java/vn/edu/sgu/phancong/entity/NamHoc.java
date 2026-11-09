package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNamHoc;

@Entity
@Table(name = "nam_hoc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NamHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String ten;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    private TrangThaiNamHoc trangThai;
}

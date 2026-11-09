package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.enums.HeDaoTao;

@Entity
@Table(name = "chuong_trinh_dao_tao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChuongTrinhDaoTao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String ma;

    @Column(nullable = false, length = 150)
    private String ten;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private HeDaoTao he;

    @Column(name = "khoa_tuyen", nullable = false, length = 10)
    private String khoaTuyen;
}

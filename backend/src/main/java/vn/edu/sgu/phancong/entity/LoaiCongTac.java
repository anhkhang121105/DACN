package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "loai_cong_tac")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoaiCongTac {

    @Id
    @Column(length = 10)
    private String ma;

    @Column(nullable = false, length = 100)
    private String ten;

    @Column(name = "so_tiet_mac_dinh", nullable = false, precision = 6, scale = 1)
    private BigDecimal soTietMacDinh;
}

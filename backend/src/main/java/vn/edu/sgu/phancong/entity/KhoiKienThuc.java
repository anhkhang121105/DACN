package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "khoi_kien_thuc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KhoiKienThuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String ten;
}

package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bo_mon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoMon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String ma;

    @Column(nullable = false, length = 100)
    private String ten;
}

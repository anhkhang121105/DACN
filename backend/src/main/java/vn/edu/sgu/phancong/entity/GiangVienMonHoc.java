package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.id.GiangVienMonHocId;

@Entity
@Table(name = "giang_vien_mon_hoc")
@IdClass(GiangVienMonHocId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiangVienMonHoc {

    @Id
    @Column(name = "giang_vien_id")
    private Integer giangVienId;

    @Id
    @Column(name = "mon_hoc_id")
    private Integer monHocId;

    @Column(name = "co_the_day_lt", nullable = false)
    private Boolean coTheDayLt = true;

    @Column(name = "co_the_day_th", nullable = false)
    private Boolean coTheDayTh = true;
}

package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.id.CtdtMonHocId;

@Entity
@Table(name = "ctdt_mon_hoc")
@IdClass(CtdtMonHocId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CtdtMonHoc {

    @Id
    @Column(name = "ctdt_id")
    private Integer ctdtId;

    @Id
    @Column(name = "mon_hoc_id")
    private Integer monHocId;

    @Column(name = "hk_du_kien")
    private Integer hkDuKien;
}

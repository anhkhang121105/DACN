package vn.edu.sgu.phancong.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.sgu.phancong.entity.id.LichRanhBanId;

@Entity
@Table(name = "lich_ranh_ban")
@IdClass(LichRanhBanId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichRanhBan {

    @Id
    @Column(name = "giang_vien_id")
    private Integer giangVienId;

    @Id
    @Column(name = "hoc_ky_id")
    private Integer hocKyId;

    @Id
    @Column(name = "thu")
    private Integer thu;

    @Id
    @Column(name = "ca")
    private Integer ca;

    @Column(nullable = false)
    private Boolean ranh = true;
}

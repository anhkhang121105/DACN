package vn.edu.sgu.phancong.entity.id;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LichRanhBanId implements Serializable {
    private Integer giangVienId;
    private Integer hocKyId;
    private Integer thu;
    private Integer ca;
}

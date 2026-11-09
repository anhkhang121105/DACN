package vn.edu.sgu.phancong.entity.id;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class GiangVienMonHocId implements Serializable {
    private Integer giangVienId;
    private Integer monHocId;
}

package vn.edu.sgu.phancong.entity.id;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CtdtMonHocId implements Serializable {
    private Integer ctdtId;
    private Integer monHocId;
}

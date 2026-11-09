package vn.edu.sgu.phancong.dto.phancong;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhanCongRequest {

    @NotNull(message = "giangVienId không được để trống")
    private Integer giangVienId;

    private Boolean isFixed;
}

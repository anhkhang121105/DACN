package vn.edu.sgu.phancong.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshRequest {

    @NotBlank(message = "refreshToken không được để trống")
    private String refreshToken;
}

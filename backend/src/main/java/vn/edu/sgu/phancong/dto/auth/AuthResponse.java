package vn.edu.sgu.phancong.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
    private String username;
    private String vaiTro;
    private Integer giangVienId;
    private Integer boMonId;
    private String hoTen;
}

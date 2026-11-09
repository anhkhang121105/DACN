package vn.edu.sgu.phancong.dto.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfo {
    private Integer id;
    private String username;
    private String vaiTro;
    private Integer giangVienId;
    private Integer boMonId;
    private String hoTen;
    private String hocVi;
    private String maCb;
}

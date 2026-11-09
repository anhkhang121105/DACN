package vn.edu.sgu.phancong.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import vn.edu.sgu.phancong.entity.NguoiDung;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Integer id;
    private final String username;
    private final String password;
    private final String vaiTro;
    private final Integer giangVienId;
    private final Integer boMonId;

    public UserPrincipal(NguoiDung nd) {
        this.id = nd.getId();
        this.username = nd.getUsername();
        this.password = nd.getPasswordHash();
        this.vaiTro = nd.getVaiTro().name();
        this.giangVienId = nd.getGiangVien() != null ? nd.getGiangVien().getId() : null;
        this.boMonId = nd.getGiangVien() != null && nd.getGiangVien().getBoMon() != null
                ? nd.getGiangVien().getBoMon().getId() : null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

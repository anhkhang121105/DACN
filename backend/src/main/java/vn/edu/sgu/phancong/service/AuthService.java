package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import vn.edu.sgu.phancong.dto.auth.AuthResponse;
import vn.edu.sgu.phancong.dto.auth.LoginRequest;
import vn.edu.sgu.phancong.dto.auth.UserInfo;
import vn.edu.sgu.phancong.entity.NguoiDung;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.repository.NguoiDungRepository;
import vn.edu.sgu.phancong.security.CustomUserDetailsService;
import vn.edu.sgu.phancong.security.JwtService;
import vn.edu.sgu.phancong.security.SecurityUtils;
import vn.edu.sgu.phancong.security.UserPrincipal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final NguoiDungRepository nguoiDungRepository;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails details = userDetailsService.loadUserByUsername(request.getUsername());
        UserPrincipal principal = (UserPrincipal) details;
        NguoiDung nd = nguoiDungRepository.findByUsername(principal.getUsername()).orElseThrow();
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(principal))
                .refreshToken(jwtService.generateRefreshToken(principal))
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .username(principal.getUsername())
                .vaiTro(principal.getVaiTro())
                .giangVienId(principal.getGiangVienId())
                .boMonId(principal.getBoMonId())
                .hoTen(nd.getGiangVien() != null ? nd.getGiangVien().getHoTen() : nd.getUsername())
                .build();
    }

    public AuthResponse refresh(String refreshToken) {
        String type = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(type)) {
            throw new BadRequestException("Token không phải refresh token");
        }
        String username = jwtService.extractUsername(refreshToken);
        UserPrincipal principal = (UserPrincipal) userDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(refreshToken, principal)) {
            throw new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn");
        }
        NguoiDung nd = nguoiDungRepository.findByUsername(username).orElseThrow();
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(principal))
                .refreshToken(jwtService.generateRefreshToken(principal))
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessExpirationMs() / 1000)
                .username(principal.getUsername())
                .vaiTro(principal.getVaiTro())
                .giangVienId(principal.getGiangVienId())
                .boMonId(principal.getBoMonId())
                .hoTen(nd.getGiangVien() != null ? nd.getGiangVien().getHoTen() : nd.getUsername())
                .build();
    }

    public UserInfo me() {
        UserPrincipal principal = SecurityUtils.getCurrentUser();
        NguoiDung nd = nguoiDungRepository.findByUsername(principal.getUsername()).orElseThrow();
        return UserInfo.builder()
                .id(nd.getId())
                .username(nd.getUsername())
                .vaiTro(nd.getVaiTro().name())
                .giangVienId(principal.getGiangVienId())
                .boMonId(principal.getBoMonId())
                .hoTen(nd.getGiangVien() != null ? nd.getGiangVien().getHoTen() : nd.getUsername())
                .hocVi(nd.getGiangVien() != null ? nd.getGiangVien().getHocVi() : null)
                .maCb(nd.getGiangVien() != null ? nd.getGiangVien().getMaCb() : null)
                .build();
    }
}

package com.fcap.fcap_backend.controller;

import java.util.Map;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fcap.fcap_backend.dto.LoginRequest;
import com.fcap.fcap_backend.dto.LoginResponse;
import com.fcap.fcap_backend.entity.GiangVien;
import com.fcap.fcap_backend.entity.TaiKhoan;
import com.fcap.fcap_backend.error.ApiException;
import com.fcap.fcap_backend.repository.GiangVienRepository;
import com.fcap.fcap_backend.repository.TaiKhoanRepository;
import com.fcap.fcap_backend.security.JwtService;

@RestController @RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
  private final TaiKhoanRepository taiKhoanRepo;
  private final GiangVienRepository gvRepo;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  @GetMapping("/ping")
  public Map<String, String> ping() { return Map.of("status", "ok"); }

  @PostMapping("/auth/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest req) {
    ApiException bad = new ApiException(HttpStatus.UNAUTHORIZED,
        "BAD_CREDENTIALS", "Sai tài khoản hoặc mật khẩu");   // cùng một thông báo cho cả hai trường hợp
    TaiKhoan tk = taiKhoanRepo.findByTenDangNhap(req.username()).orElseThrow(() -> bad);
    if (!encoder.matches(req.password(), tk.getMatKhauHash())) throw bad;

    String dept = null, hoTen = null;
    if (tk.getMaGV() != null) {
      GiangVien gv = gvRepo.findById(tk.getMaGV()).orElse(null);
      if (gv != null) { dept = gv.getMaBoMon(); hoTen = gv.getHoTen(); }
    }
    return new LoginResponse(jwt.create(tk, dept), tk.getVaiTro(), tk.getMaTK(),
                             tk.getMaGV(), dept, hoTen);
  }
}
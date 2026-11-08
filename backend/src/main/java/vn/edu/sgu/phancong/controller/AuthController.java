package vn.edu.sgu.phancong.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.dto.auth.AuthResponse;
import vn.edu.sgu.phancong.dto.auth.LoginRequest;
import vn.edu.sgu.phancong.dto.auth.RefreshRequest;
import vn.edu.sgu.phancong.dto.auth.UserInfo;
import vn.edu.sgu.phancong.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.getRefreshToken());
    }

    @GetMapping("/me")
    public UserInfo me() {
        return authService.me();
    }
}

package com.fcap.fcap_backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fcap.fcap_backend.entity.TaiKhoan;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  private final SecretKey key; private final long expMs;
  public JwtService(@Value("${app.jwt.secret}") String secret,
                    @Value("${app.jwt.expiration-minutes}") long expMinutes) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.expMs = expMinutes * 60_000;
  }
  public String create(TaiKhoan tk, String departmentId) {
    Date now = new Date();
    return Jwts.builder()
        .subject(String.valueOf(tk.getMaTK()))
        .claim("role", tk.getVaiTro())
        .claim("lecturerId", tk.getMaGV())
        .claim("departmentId", departmentId)
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expMs))
        .signWith(key)
        .compact();
  }
}
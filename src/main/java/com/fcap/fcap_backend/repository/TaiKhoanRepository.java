package com.fcap.fcap_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fcap.fcap_backend.entity.TaiKhoan;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Long> {
  Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);
}
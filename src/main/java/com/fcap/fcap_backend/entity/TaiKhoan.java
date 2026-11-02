package com.fcap.fcap_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "TaiKhoan")
@Getter @Setter
public class TaiKhoan {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "MaTK") private Long maTK;
  @Column(name = "TenDangNhap") private String tenDangNhap;
  @Column(name = "MatKhauHash") private String matKhauHash;
  @Column(name = "VaiTro") private String vaiTro;
  @Column(name = "MaGV") private String maGV;
}
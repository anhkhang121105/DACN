package com.fcap.fcap_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity @Table(name = "GiangVien")
@Getter @Setter
public class GiangVien {
  @Id @Column(name = "MaGV") private String maGV;
  @Column(name = "HoTen") private String hoTen;
  @Column(name = "MaBoMon") private String maBoMon;
}
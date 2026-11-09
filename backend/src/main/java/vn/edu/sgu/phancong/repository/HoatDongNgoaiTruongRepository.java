package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.HoatDongNgoaiTruong;

import java.util.List;

public interface HoatDongNgoaiTruongRepository extends JpaRepository<HoatDongNgoaiTruong, Integer> {
    List<HoatDongNgoaiTruong> findByNamHocId(Integer namHocId);
}

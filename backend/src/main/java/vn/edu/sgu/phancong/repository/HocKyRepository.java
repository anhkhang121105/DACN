package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.HocKy;

import java.util.List;
import java.util.Optional;

public interface HocKyRepository extends JpaRepository<HocKy, Integer> {
    List<HocKy> findByNamHocIdOrderBySoHkAsc(Integer namHocId);

    Optional<HocKy> findByNamHocIdAndSoHk(Integer namHocId, Integer soHk);
}

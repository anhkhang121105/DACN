package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.NamHoc;

import java.util.Optional;

public interface NamHocRepository extends JpaRepository<NamHoc, Integer> {
    Optional<NamHoc> findByTen(String ten);
}

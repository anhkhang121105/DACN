package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.MonHoc;

import java.util.List;
import java.util.Optional;

public interface MonHocRepository extends JpaRepository<MonHoc, Integer> {
    List<MonHoc> findByBoMonId(Integer boMonId);

    Optional<MonHoc> findByMaHp(String maHp);

    boolean existsByMaHp(String maHp);
}

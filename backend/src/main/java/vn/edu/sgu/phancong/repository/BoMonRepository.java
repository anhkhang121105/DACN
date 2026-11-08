package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.BoMon;

import java.util.Optional;

public interface BoMonRepository extends JpaRepository<BoMon, Integer> {
    Optional<BoMon> findByMa(String ma);
}

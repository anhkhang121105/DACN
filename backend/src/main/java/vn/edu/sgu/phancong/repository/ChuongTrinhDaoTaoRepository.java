package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.ChuongTrinhDaoTao;

import java.util.Optional;

public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, Integer> {
    Optional<ChuongTrinhDaoTao> findByMa(String ma);
}

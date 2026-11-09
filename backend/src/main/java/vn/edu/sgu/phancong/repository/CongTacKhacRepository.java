package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.CongTacKhac;

import java.util.List;

public interface CongTacKhacRepository extends JpaRepository<CongTacKhac, Integer> {
    List<CongTacKhac> findByGiangVienIdAndNamHocId(Integer giangVienId, Integer namHocId);

    List<CongTacKhac> findByNamHocId(Integer namHocId);
}

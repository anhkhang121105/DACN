package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.KeHoachBoMon;
import vn.edu.sgu.phancong.entity.enums.TrangThaiKeHoach;

import java.util.List;
import java.util.Optional;

public interface KeHoachBoMonRepository extends JpaRepository<KeHoachBoMon, Integer> {
    List<KeHoachBoMon> findByNamHocId(Integer namHocId);

    Optional<KeHoachBoMon> findByNamHocIdAndBoMonId(Integer namHocId, Integer boMonId);

    boolean existsByNamHocIdAndBoMonId(Integer namHocId, Integer boMonId);

    long countByNamHocIdAndTrangThai(Integer namHocId, TrangThaiKeHoach trangThai);
}

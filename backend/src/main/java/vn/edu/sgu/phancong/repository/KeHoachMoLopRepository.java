package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.KeHoachMoLop;

import java.util.List;
import java.util.Optional;

public interface KeHoachMoLopRepository extends JpaRepository<KeHoachMoLop, Integer> {
    List<KeHoachMoLop> findByHocKyId(Integer hocKyId);

    List<KeHoachMoLop> findByHocKyNamHocId(Integer namHocId);

    Optional<KeHoachMoLop> findByHocKyIdAndMonHocIdAndKhoaHienThi(Integer hocKyId, Integer monHocId, String khoaHienThi);

    boolean existsByHocKyIdAndMonHocId(Integer hocKyId, Integer monHocId);
}

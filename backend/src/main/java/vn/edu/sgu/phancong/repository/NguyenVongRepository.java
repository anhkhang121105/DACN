package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.NguyenVong;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNguyenVong;

import java.util.List;
import java.util.Optional;

public interface NguyenVongRepository extends JpaRepository<NguyenVong, Integer> {
    List<NguyenVong> findByGiangVienIdAndHocKyId(Integer giangVienId, Integer hocKyId);

    List<NguyenVong> findByHocKyId(Integer hocKyId);

    List<NguyenVong> findByHocKyIdAndGiangVienBoMonId(Integer hocKyId, Integer boMonId);

    List<NguyenVong> findByHocKyIdAndTrangThai(Integer hocKyId, TrangThaiNguyenVong trangThai);

    Optional<NguyenVong> findByGiangVienIdAndHocKyIdAndMonHocId(Integer giangVienId, Integer hocKyId, Integer monHocId);

    boolean existsByGiangVienIdAndHocKyIdAndMonHocId(Integer giangVienId, Integer hocKyId, Integer monHocId);
}

package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.entity.GiangVienMonHoc;
import vn.edu.sgu.phancong.entity.id.GiangVienMonHocId;

import java.util.List;

public interface GiangVienMonHocRepository extends JpaRepository<GiangVienMonHoc, GiangVienMonHocId> {
    List<GiangVienMonHoc> findByGiangVienId(Integer giangVienId);

    List<GiangVienMonHoc> findByMonHocId(Integer monHocId);

    boolean existsByGiangVienIdAndMonHocId(Integer giangVienId, Integer monHocId);

    @Transactional
    @Modifying
    @Query("DELETE FROM GiangVienMonHoc g WHERE g.giangVienId = :giangVienId AND g.monHocId = :monHocId")
    void deleteByGiangVienIdAndMonHocId(Integer giangVienId, Integer monHocId);
}

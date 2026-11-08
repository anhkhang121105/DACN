package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.enums.LoaiGiangVien;

import java.util.List;
import java.util.Optional;

public interface GiangVienRepository extends JpaRepository<GiangVien, Integer> {
    List<GiangVien> findByBoMonId(Integer boMonId);

    List<GiangVien> findByLoai(LoaiGiangVien loai);

    Optional<GiangVien> findByMaCb(String maCb);

    boolean existsByMaCb(String maCb);

    long countByBoMonId(Integer boMonId);

    @Query("SELECT g FROM GiangVien g WHERE g.boMon.id = :boMonId OR g.id IN " +
            "(SELECT gvmh.giangVienId FROM GiangVienMonHoc gvmh WHERE gvmh.monHocId IN " +
            "(SELECT m.id FROM MonHoc m WHERE m.boMon.id = :boMonId))")
    List<GiangVien> findAvailableForBoMon(@Param("boMonId") Integer boMonId);
}

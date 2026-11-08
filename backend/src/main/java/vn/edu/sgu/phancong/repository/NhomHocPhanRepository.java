package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.sgu.phancong.entity.NhomHocPhan;

import java.math.BigDecimal;
import java.util.List;

public interface NhomHocPhanRepository extends JpaRepository<NhomHocPhan, Integer> {

    List<NhomHocPhan> findByKeHoachIdOrderBySoNhomAscLoaiAsc(Integer keHoachId);

    List<NhomHocPhan> findByKeHoachHocKyId(Integer hocKyId);

    List<NhomHocPhan> findByKeHoachHocKyNamHocId(Integer namHocId);

    List<NhomHocPhan> findByGiangVienId(Integer giangVienId);

    List<NhomHocPhan> findByKeHoachIdAndGiangVienIsNull(Integer keHoachId);

    long countByKeHoachId(Integer keHoachId);

    long countByGiangVienId(Integer giangVienId);

    void deleteByKeHoachId(Integer keHoachId);

    @Query("SELECT COALESCE(SUM(n.soTietThucTe), 0) FROM NhomHocPhan n " +
            "WHERE n.giangVien.id = :giangVienId AND n.keHoach.hocKy.namHoc.id = :namHocId")
    BigDecimal sumTietByGiangVienAndNamHoc(@Param("giangVienId") Integer giangVienId,
                                           @Param("namHocId") Integer namHocId);

    @Query("SELECT COALESCE(SUM(n.soTietThucTe), 0) FROM NhomHocPhan n " +
            "WHERE n.giangVien.id = :giangVienId AND n.keHoach.hocKy.id = :hocKyId")
    BigDecimal sumTietByGiangVienAndHocKy(@Param("giangVienId") Integer giangVienId,
                                          @Param("hocKyId") Integer hocKyId);

    @Query("SELECT n.keHoach.monHoc.boMon.id, COUNT(n) FROM NhomHocPhan n " +
            "WHERE n.keHoach.hocKy.id = :hocKyId AND n.giangVien IS NULL GROUP BY n.keHoach.monHoc.boMon.id")
    List<Object[]> countUnassignedByBoMon(@Param("hocKyId") Integer hocKyId);
}

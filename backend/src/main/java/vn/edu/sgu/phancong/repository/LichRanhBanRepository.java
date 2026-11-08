package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.LichRanhBan;
import vn.edu.sgu.phancong.entity.id.LichRanhBanId;

import java.util.List;

public interface LichRanhBanRepository extends JpaRepository<LichRanhBan, LichRanhBanId> {
    List<LichRanhBan> findByGiangVienIdAndHocKyId(Integer giangVienId, Integer hocKyId);

    List<LichRanhBan> findByHocKyId(Integer hocKyId);

    void deleteByGiangVienIdAndHocKyId(Integer giangVienId, Integer hocKyId);
}

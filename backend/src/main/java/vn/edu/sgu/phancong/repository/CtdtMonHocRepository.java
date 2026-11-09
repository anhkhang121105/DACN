package vn.edu.sgu.phancong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.edu.sgu.phancong.entity.CtdtMonHoc;
import vn.edu.sgu.phancong.entity.id.CtdtMonHocId;

import java.util.List;

public interface CtdtMonHocRepository extends JpaRepository<CtdtMonHoc, CtdtMonHocId> {
    List<CtdtMonHoc> findByCtdtId(Integer ctdtId);

    List<CtdtMonHoc> findByMonHocId(Integer monHocId);

    void deleteByCtdtId(Integer ctdtId);
}

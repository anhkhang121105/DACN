package vn.edu.sgu.phancong.rules;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.GiangVienMonHoc;
import vn.edu.sgu.phancong.entity.HocKy;
import vn.edu.sgu.phancong.entity.LichRanhBan;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.entity.ThamSoLuat;
import vn.edu.sgu.phancong.repository.GiangVienMonHocRepository;
import vn.edu.sgu.phancong.repository.GiangVienRepository;
import vn.edu.sgu.phancong.repository.HocKyRepository;
import vn.edu.sgu.phancong.repository.LichRanhBanRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;
import vn.edu.sgu.phancong.repository.ThamSoLuatRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class RuleContextLoader {

    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final GiangVienRepository giangVienRepository;
    private final GiangVienMonHocRepository giangVienMonHocRepository;
    private final LichRanhBanRepository lichRanhBanRepository;
    private final HocKyRepository hocKyRepository;
    private final ThamSoLuatRepository thamSoLuatRepository;

    public RuleContext loadForNamHoc(Integer namHocId) {
        List<NhomHocPhan> nhoms = nhomHocPhanRepository.findByKeHoachHocKyNamHocId(namHocId);
        return build(namHocId, nhoms);
    }

    public RuleContext build(Integer namHocId, List<NhomHocPhan> nhoms) {
        Map<Integer, GiangVien> giangVienById = new HashMap<>();
        for (GiangVien gv : giangVienRepository.findAll()) {
            giangVienById.put(gv.getId(), gv);
        }

        Map<String, GiangVienMonHoc> chuyenMon = new HashMap<>();
        for (GiangVienMonHoc gvmh : giangVienMonHocRepository.findAll()) {
            chuyenMon.put(gvmh.getGiangVienId() + ":" + gvmh.getMonHocId(), gvmh);
        }

        Map<Integer, BigDecimal> workload = new HashMap<>();
        Map<Integer, Integer> nhomAssignee = new HashMap<>();
        Map<String, Integer> slotOwners = new HashMap<>();
        for (NhomHocPhan n : nhoms) {
            if (n.getGiangVien() != null) {
                Integer gvId = n.getGiangVien().getId();
                workload.merge(gvId, n.tinhTietThucTe(), BigDecimal::add);
                nhomAssignee.put(n.getId(), gvId);
                if (n.getThu() != null && n.getCa() != null) {
                    String key = RuleContext.slotKey(gvId, n.getKeHoach().getHocKy().getId(), n.getThu(), n.getCa());
                    slotOwners.put(key, n.getId());
                }
            }
        }

        Set<String> busySlots = new HashSet<>();
        for (HocKy hk : hocKyRepository.findByNamHocIdOrderBySoHkAsc(namHocId)) {
            for (LichRanhBan lrb : lichRanhBanRepository.findByHocKyId(hk.getId())) {
                if (!Boolean.TRUE.equals(lrb.getRanh())) {
                    busySlots.add(RuleContext.slotKey(lrb.getGiangVienId(), hk.getId(), lrb.getThu(), lrb.getCa()));
                }
            }
        }

        Map<String, BigDecimal> thamSo = new HashMap<>();
        for (ThamSoLuat ts : thamSoLuatRepository.findAll()) {
            thamSo.put(ts.getMa(), ts.getGiaTri());
        }

        return RuleContext.builder()
                .giangVienById(giangVienById)
                .chuyenMon(chuyenMon)
                .workloadByGv(workload)
                .busySlots(busySlots)
                .nhomAssignee(nhomAssignee)
                .slotOwners(slotOwners)
                .thamSo(thamSo)
                .build();
    }
}

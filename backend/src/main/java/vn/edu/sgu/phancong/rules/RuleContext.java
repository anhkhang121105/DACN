package vn.edu.sgu.phancong.rules;

import lombok.Builder;
import lombok.Getter;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.GiangVienMonHoc;
import vn.edu.sgu.phancong.entity.enums.LoaiNhom;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

@Getter
@Builder
public class RuleContext {

    private final Map<Integer, GiangVien> giangVienById;
    private final Map<String, GiangVienMonHoc> chuyenMon;
    private final Map<Integer, BigDecimal> workloadByGv;
    private final Set<String> busySlots;
    private final Map<Integer, Integer> nhomAssignee;
    private final Map<String, Integer> slotOwners;
    private final Map<String, BigDecimal> thamSo;

    public boolean canTeach(Integer giangVienId, Integer monHocId, LoaiNhom loai) {
        GiangVienMonHoc mapping = chuyenMon.get(giangVienId + ":" + monHocId);
        if (mapping == null) {
            return false;
        }
        if (loai == LoaiNhom.TH) {
            return Boolean.TRUE.equals(mapping.getCoTheDayTh());
        }
        return Boolean.TRUE.equals(mapping.getCoTheDayLt());
    }

    public BigDecimal workload(Integer giangVienId) {
        return workloadByGv.getOrDefault(giangVienId, BigDecimal.ZERO);
    }

    public boolean isBusy(Integer giangVienId, Integer hocKyId, Integer thu, Integer ca) {
        return busySlots.contains(slotKey(giangVienId, hocKyId, thu, ca));
    }

    public Integer assigneeOf(Integer nhomId) {
        return nhomAssignee.get(nhomId);
    }

    public Integer slotOwner(Integer giangVienId, Integer hocKyId, Integer thu, Integer ca) {
        return slotOwners.get(slotKey(giangVienId, hocKyId, thu, ca));
    }

    public BigDecimal thamSo(String ma, BigDecimal defaultValue) {
        return thamSo.getOrDefault(ma, defaultValue);
    }

    public static String slotKey(Integer giangVienId, Integer hocKyId, Integer thu, Integer ca) {
        return giangVienId + "-" + hocKyId + "-" + thu + "-" + ca;
    }
}

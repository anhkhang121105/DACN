package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.sgu.phancong.dto.report.TaiLuongDto;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.CongTacKhac;
import vn.edu.sgu.phancong.repository.CongTacKhacRepository;
import vn.edu.sgu.phancong.repository.GiangVienRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkloadService {

    private final GiangVienRepository giangVienRepository;
    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final CongTacKhacRepository congTacKhacRepository;

    public TaiLuongDto tinhTaiLuong(Integer giangVienId, Integer namHocId) {
        GiangVien gv = giangVienRepository.findById(giangVienId)
                .orElseThrow(() -> new vn.edu.sgu.phancong.exception.NotFoundException("Giảng viên", giangVienId));
        BigDecimal giangDay = nhomHocPhanRepository.sumTietByGiangVienAndNamHoc(giangVienId, namHocId);
        BigDecimal congTac = congTacKhacRepository.findByGiangVienIdAndNamHocId(giangVienId, namHocId).stream()
                .map(CongTacKhac::getSoTiet)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (giangDay == null) {
            giangDay = BigDecimal.ZERO;
        }
        return build(gv, giangDay, congTac);
    }

    public List<TaiLuongDto> taiLuongBoMon(Integer boMonId, Integer namHocId) {
        List<TaiLuongDto> result = new ArrayList<>();
        for (GiangVien gv : giangVienRepository.findByBoMonId(boMonId)) {
            result.add(tinhTaiLuong(gv.getId(), namHocId));
        }
        return result;
    }

    public List<TaiLuongDto> taiLuongKhoa(Integer namHocId) {
        List<TaiLuongDto> result = new ArrayList<>();
        for (GiangVien gv : giangVienRepository.findAll()) {
            if (gv.getLoai() == vn.edu.sgu.phancong.entity.enums.LoaiGiangVien.CO_HUU) {
                result.add(tinhTaiLuong(gv.getId(), namHocId));
            }
        }
        return result;
    }

    private TaiLuongDto build(GiangVien gv, BigDecimal giangDay, BigDecimal congTac) {
        BigDecimal tong = giangDay.add(congTac);
        BigDecimal max = gv.getDinhMucMax() != null ? gv.getDinhMucMax() : BigDecimal.valueOf(350);
        BigDecimal phanTram = max.signum() > 0
                ? tong.multiply(BigDecimal.valueOf(100)).divide(max, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        String trangThai;
        if (tong.compareTo(gv.getDinhMucMax()) > 0) {
            trangThai = "VUOT_TRAN";
        } else if (tong.compareTo(gv.getDinhMucMin()) < 0) {
            trangThai = "DUOI_DINH_MUC";
        } else {
            trangThai = "DAT";
        }
        return TaiLuongDto.builder()
                .giangVienId(gv.getId())
                .maCb(gv.getMaCb())
                .hoTen(gv.getHoTen())
                .hocVi(gv.getHocVi())
                .boMonId(gv.getBoMon() != null ? gv.getBoMon().getId() : null)
                .boMonTen(gv.getBoMon() != null ? gv.getBoMon().getTen() : null)
                .tietGiangDay(giangDay.setScale(2, RoundingMode.HALF_UP))
                .tietCongTacKhac(congTac.setScale(2, RoundingMode.HALF_UP))
                .tongTiet(tong.setScale(2, RoundingMode.HALF_UP))
                .dinhMucMin(gv.getDinhMucMin())
                .dinhMucMax(gv.getDinhMucMax())
                .phanTramTai(phanTram)
                .trangThai(trangThai)
                .build();
    }
}

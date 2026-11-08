package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.entity.HocKy;
import vn.edu.sgu.phancong.entity.KeHoachMoLop;
import vn.edu.sgu.phancong.entity.MonHoc;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.entity.enums.LoaiNhom;
import vn.edu.sgu.phancong.entity.enums.NguonPhanCong;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.HocKyRepository;
import vn.edu.sgu.phancong.repository.KeHoachMoLopRepository;
import vn.edu.sgu.phancong.repository.MonHocRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeHoachMoLopService {

    private final KeHoachMoLopRepository keHoachRepository;
    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final HocKyRepository hocKyRepository;
    private final MonHocRepository monHocRepository;

    @Transactional
    public KeHoachMoLop create(KeHoachMoLop body, boolean autoSinhNhom) {
        if (body.getHocKy() == null || body.getHocKy().getId() == null) {
            throw new BadRequestException("Học kỳ không được để trống");
        }
        if (body.getMonHoc() == null || body.getMonHoc().getId() == null) {
            throw new BadRequestException("Môn học không được để trống");
        }
        HocKy hocKy = hocKyRepository.findById(body.getHocKy().getId())
                .orElseThrow(() -> new NotFoundException("Học kỳ", body.getHocKy().getId()));
        MonHoc monHoc = monHocRepository.findById(body.getMonHoc().getId())
                .orElseThrow(() -> new NotFoundException("Môn học", body.getMonHoc().getId()));
        body.setId(null);
        body.setHocKy(hocKy);
        body.setMonHoc(monHoc);
        if (body.getMaxSvNhom() == null || body.getMaxSvNhom() <= 0) {
            body.setMaxSvNhom(50);
        }
        KeHoachMoLop saved = keHoachRepository.save(body);
        if (autoSinhNhom) {
            sinhNhomInternal(saved, true);
        }
        return saved;
    }

    @Transactional
    public List<NhomHocPhan> sinhNhom(Integer keHoachId, boolean force) {
        KeHoachMoLop keHoach = keHoachRepository.findById(keHoachId)
                .orElseThrow(() -> new NotFoundException("Kế hoạch mở lớp", keHoachId));
        return sinhNhomInternal(keHoach, force);
    }

    @Transactional
    public List<NhomHocPhan> sinhNhomInternal(KeHoachMoLop keHoach, boolean force) {
        long existing = nhomHocPhanRepository.countByKeHoachId(keHoach.getId());
        if (existing > 0) {
            boolean hasAssigned = nhomHocPhanRepository.findByKeHoachIdOrderBySoNhomAscLoaiAsc(keHoach.getId())
                    .stream().anyMatch(n -> n.getGiangVien() != null);
            if (!force || hasAssigned) {
                throw new BadRequestException("Nhóm học phần đã tồn tại hoặc đã được phân công. "
                        + "Dùng force=true chỉ khi chưa có phân công.");
            }
            nhomHocPhanRepository.deleteByKeHoachId(keHoach.getId());
        }

        MonHoc monHoc = keHoach.getMonHoc();
        int soNhom = keHoach.tinhSoNhom();
        if (soNhom <= 0) {
            throw new BadRequestException("Sĩ số hoặc sĩ số tối đa không hợp lệ");
        }
        BigDecimal heSo = monHoc.getHeSoHp() != null ? monHoc.getHeSoHp() : BigDecimal.ONE;
        int tietLt = monHoc.getTietLt() == null ? 0 : monHoc.getTietLt();
        int tietBt = monHoc.getTietBt() == null ? 0 : monHoc.getTietBt();
        int tietTh = monHoc.getTietTh() == null ? 0 : monHoc.getTietTh();

        List<NhomHocPhan> result = new ArrayList<>();
        if (tietTh > 0) {
            for (int i = 1; i <= soNhom; i++) {
                result.add(make(keHoach, i, LoaiNhom.LT, BigDecimal.valueOf(tietLt), heSo));
            }
            int thGroups = soNhom * 2;
            BigDecimal tietThMoiNhom = BigDecimal.valueOf(tietTh)
                    .divide(BigDecimal.valueOf(2), 1, RoundingMode.HALF_UP);
            for (int i = 1; i <= thGroups; i++) {
                result.add(make(keHoach, i, LoaiNhom.TH, tietThMoiNhom, heSo));
            }
        } else {
            BigDecimal tong = BigDecimal.valueOf(tietLt + tietBt);
            for (int i = 1; i <= soNhom; i++) {
                result.add(make(keHoach, i, LoaiNhom.LT_TH, tong, heSo));
            }
        }
        return nhomHocPhanRepository.saveAll(result);
    }

    private NhomHocPhan make(KeHoachMoLop keHoach, int soNhom, LoaiNhom loai,
                             BigDecimal soTiet, BigDecimal heSo) {
        return NhomHocPhan.builder()
                .keHoach(keHoach)
                .soNhom(soNhom)
                .loai(loai)
                .soTietThucHien(soTiet)
                .heSo(heSo)
                .nguon(NguonPhanCong.CHUA)
                .isFixed(false)
                .build();
    }
}

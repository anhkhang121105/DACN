package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.dto.CsrResult;
import vn.edu.sgu.phancong.entity.BoMon;
import vn.edu.sgu.phancong.entity.KeHoachBoMon;
import vn.edu.sgu.phancong.entity.NamHoc;
import vn.edu.sgu.phancong.entity.enums.TrangThaiKeHoach;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNamHoc;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.BoMonRepository;
import vn.edu.sgu.phancong.repository.KeHoachBoMonRepository;
import vn.edu.sgu.phancong.repository.NamHocRepository;
import vn.edu.sgu.phancong.security.SecurityUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeHoachBoMonService {

    private final KeHoachBoMonRepository repository;
    private final NamHocRepository namHocRepository;
    private final BoMonRepository boMonRepository;
    private final CsrService csrService;

    public List<KeHoachBoMon> findAll(Integer namHocId, Integer boMonId) {
        List<KeHoachBoMon> all = namHocId != null ? repository.findByNamHocId(namHocId) : repository.findAll();
        if (boMonId != null) {
            return all.stream().filter(k -> k.getBoMon().getId().equals(boMonId)).toList();
        }
        return all;
    }

    @Transactional
    public KeHoachBoMon taoHoacLay(Integer namHocId, Integer boMonId) {
        return repository.findByNamHocIdAndBoMonId(namHocId, boMonId).orElseGet(() -> {
            NamHoc namHoc = namHocRepository.findById(namHocId)
                    .orElseThrow(() -> new NotFoundException("Năm học", namHocId));
            BoMon boMon = boMonRepository.findById(boMonId)
                    .orElseThrow(() -> new NotFoundException("Bộ môn", boMonId));
            KeHoachBoMon k = KeHoachBoMon.builder()
                    .namHoc(namHoc)
                    .boMon(boMon)
                    .trangThai(TrangThaiKeHoach.DRAFT)
                    .csr(BigDecimal.ZERO)
                    .build();
            return repository.save(k);
        });
    }

    @Transactional
    public KeHoachBoMon nop(Integer id) {
        KeHoachBoMon k = get(id);
        if (k.getTrangThai() == TrangThaiKeHoach.APPROVED) {
            throw new BadRequestException("Kế hoạch đã được duyệt");
        }
        CsrResult csr = csrService.tinhCsr(k.getBoMon().getId(), k.getNamHoc().getId());
        k.setCsr(csr.getCsr());
        if (csr.getCsr().compareTo(BigDecimal.valueOf(100)) < 0) {
            repository.save(k);
            throw new BadRequestException("Chỉ được nộp khi CSR = 100%. Hiện tại: " + csr.getCsr() + "%");
        }
        k.setTrangThai(TrangThaiKeHoach.SUBMITTED);
        k.setNguoiNop(SecurityUtils.getCurrentUserId());
        k.setNgayNop(LocalDateTime.now());
        k.setLyDoTraVe(null);
        return repository.save(k);
    }

    @Transactional
    public KeHoachBoMon duyet(Integer id) {
        KeHoachBoMon k = get(id);
        if (k.getTrangThai() != TrangThaiKeHoach.SUBMITTED) {
            throw new BadRequestException("Chỉ duyệt kế hoạch đã nộp");
        }
        k.setTrangThai(TrangThaiKeHoach.APPROVED);
        k.setNguoiDuyet(SecurityUtils.getCurrentUserId());
        k.setNgayDuyet(LocalDateTime.now());
        KeHoachBoMon saved = repository.save(k);

        tryKhoaNamHoc(k.getNamHoc().getId());
        return saved;
    }

    @Transactional
    public KeHoachBoMon traVe(Integer id, String lyDo) {
        KeHoachBoMon k = get(id);
        if (k.getTrangThai() != TrangThaiKeHoach.SUBMITTED) {
            throw new BadRequestException("Chỉ trả về kế hoạch đã nộp");
        }
        if (lyDo == null || lyDo.isBlank()) {
            throw new BadRequestException("Phải nhập lý do trả về");
        }
        k.setTrangThai(TrangThaiKeHoach.RETURNED);
        k.setLyDoTraVe(lyDo);
        k.setNguoiDuyet(SecurityUtils.getCurrentUserId());
        k.setNgayDuyet(LocalDateTime.now());
        return repository.save(k);
    }

    public CsrResult csrHienTai(Integer id) {
        KeHoachBoMon k = get(id);
        return csrService.tinhCsr(k.getBoMon().getId(), k.getNamHoc().getId());
    }

    private KeHoachBoMon get(Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Kế hoạch bộ môn", id));
    }

    private void tryKhoaNamHoc(Integer namHocId) {
        long tongBoMon = boMonRepository.count();
        long daDuyet = repository.countByNamHocIdAndTrangThai(namHocId, TrangThaiKeHoach.APPROVED);
        if (tongBoMon > 0 && daDuyet >= tongBoMon) {
            NamHoc namHoc = namHocRepository.findById(namHocId).orElse(null);
            if (namHoc != null) {
                namHoc.setTrangThai(TrangThaiNamHoc.DA_KHOA);
                namHocRepository.save(namHoc);
            }
        }
    }
}

package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.dto.report.TaiLuongDto;
import vn.edu.sgu.phancong.entity.BoMon;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.entity.enums.LoaiGiangVien;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.BoMonRepository;
import vn.edu.sgu.phancong.repository.GiangVienRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;
import vn.edu.sgu.phancong.security.SecurityUtils;
import vn.edu.sgu.phancong.service.WorkloadService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/giang-vien")
@RequiredArgsConstructor
public class GiangVienController {

    private final GiangVienRepository repository;
    private final BoMonRepository boMonRepository;
    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final WorkloadService workloadService;

    @GetMapping
    public List<GiangVien> findAll(@RequestParam(required = false) Integer boMonId,
                                   @RequestParam(required = false) LoaiGiangVien loai) {
        if (boMonId != null) {
            return repository.findByBoMonId(boMonId);
        }
        if (loai != null) {
            return repository.findByLoai(loai);
        }
        return repository.findAll();
    }

    @GetMapping("/me")
    public GiangVien me() {
        return repository.findById(SecurityUtils.getCurrentGiangVienId())
                .orElseThrow(() -> new NotFoundException("Giảng viên", SecurityUtils.getCurrentGiangVienId()));
    }

    @GetMapping("/me/tai-luong")
    public TaiLuongDto taiLuongCaNhan(@RequestParam Integer namHocId) {
        return workloadService.tinhTaiLuong(SecurityUtils.getCurrentGiangVienId(), namHocId);
    }

    @GetMapping("/me/phan-cong")
    public List<NhomHocPhan> phanCongCaNhan(@RequestParam(required = false) Integer hocKyId) {
        List<NhomHocPhan> all = nhomHocPhanRepository.findByGiangVienId(SecurityUtils.getCurrentGiangVienId());
        if (hocKyId == null) {
            return all;
        }
        return all.stream()
                .filter(n -> n.getKeHoach().getHocKy().getId().equals(hocKyId))
                .toList();
    }

    @GetMapping("/{id}")
    public GiangVien findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Giảng viên", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public GiangVien create(@RequestBody GiangVien body) {
        if (body.getMaCb() == null || body.getMaCb().isBlank()) {
            throw new BadRequestException("Mã cán bộ không được để trống");
        }
        if (repository.existsByMaCb(body.getMaCb())) {
            throw new BadRequestException("Mã cán bộ đã tồn tại: " + body.getMaCb());
        }
        body.setId(null);
        applyDefaults(body);
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public GiangVien update(@PathVariable Integer id, @RequestBody GiangVien body) {
        GiangVien existing = findOne(id);
        existing.setHoTen(body.getHoTen());
        existing.setNamSinh(body.getNamSinh());
        existing.setHocVi(body.getHocVi());
        existing.setChucDanh(body.getChucDanh());
        if (body.getLoai() != null) {
            existing.setLoai(body.getLoai());
        }
        existing.setDonViCongTac(body.getDonViCongTac());
        existing.setChuyenNganh(body.getChuyenNganh());
        if (body.getLaTapSu() != null) {
            existing.setLaTapSu(body.getLaTapSu());
        }
        if (body.getBoMon() != null && body.getBoMon().getId() != null) {
            BoMon bm = boMonRepository.findById(body.getBoMon().getId())
                    .orElseThrow(() -> new NotFoundException("Bộ môn", body.getBoMon().getId()));
            existing.setBoMon(bm);
        }
        applyDefaults(existing);
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        GiangVien existing = findOne(id);
        if (nhomHocPhanRepository.countByGiangVienId(id) > 0) {
            throw new BadRequestException("Không thể xóa giảng viên đã được phân công");
        }
        repository.delete(existing);
    }

    private void applyDefaults(GiangVien gv) {
        if (gv.getBoMon() != null && gv.getBoMon().getId() != null) {
            gv.setBoMon(boMonRepository.findById(gv.getBoMon().getId())
                    .orElseThrow(() -> new NotFoundException("Bộ môn", gv.getBoMon().getId())));
        } else {
            gv.setBoMon(null);
        }
        if (gv.getLaTapSu() == null) {
            gv.setLaTapSu(false);
        }
        if (gv.getLoai() == null) {
            gv.setLoai(LoaiGiangVien.CO_HUU);
        }
        if (gv.getDinhMucMin() == null) {
            gv.setDinhMucMin(BigDecimal.valueOf(Boolean.TRUE.equals(gv.getLaTapSu()) ? 175 : 200));
        }
        if (gv.getDinhMucMax() == null) {
            gv.setDinhMucMax(BigDecimal.valueOf(350));
        }
        if (Boolean.TRUE.equals(gv.getLaTapSu()) && gv.getDinhMucMin().compareTo(BigDecimal.valueOf(200)) == 0) {
            gv.setDinhMucMin(BigDecimal.valueOf(175));
        }
    }
}

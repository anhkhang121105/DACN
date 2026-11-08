package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.HocKy;
import vn.edu.sgu.phancong.entity.NamHoc;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNamHoc;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.HocKyRepository;
import vn.edu.sgu.phancong.repository.NamHocRepository;

import java.util.List;

@RestController
@RequestMapping("/api/hoc-ky")
@RequiredArgsConstructor
public class HocKyController {

    private final HocKyRepository repository;
    private final NamHocRepository namHocRepository;

    @GetMapping
    public List<HocKy> findAll(@RequestParam(required = false) Integer namHocId) {
        if (namHocId != null) {
            return repository.findByNamHocIdOrderBySoHkAsc(namHocId);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public HocKy findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Học kỳ", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public HocKy create(@RequestBody HocKy body) {
        body.setId(null);
        if (body.getNamHoc() == null || body.getNamHoc().getId() == null) {
            throw new NotFoundException("Năm học không được để trống");
        }
        NamHoc namHoc = namHocRepository.findById(body.getNamHoc().getId())
                .orElseThrow(() -> new NotFoundException("Năm học", body.getNamHoc().getId()));
        body.setNamHoc(namHoc);
        if (body.getMoDangKy() == null) {
            body.setMoDangKy(false);
        }
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public HocKy update(@PathVariable Integer id, @RequestBody HocKy body) {
        HocKy existing = findOne(id);
        existing.setSoHk(body.getSoHk());
        existing.setNgayBd(body.getNgayBd());
        existing.setNgayKt(body.getNgayKt());
        if (body.getMoDangKy() != null) {
            existing.setMoDangKy(body.getMoDangKy());
        }
        if (body.getNamHoc() != null && body.getNamHoc().getId() != null) {
            existing.setNamHoc(namHocRepository.findById(body.getNamHoc().getId())
                    .orElseThrow(() -> new NotFoundException("Năm học", body.getNamHoc().getId())));
        }
        return repository.save(existing);
    }

    @PatchMapping("/{id}/mo-dang-ky")
    @PreAuthorize("hasRole('TRO_LY')")
    public HocKy toggleDangKy(@PathVariable Integer id, @RequestParam(required = false) Boolean mo) {
        HocKy existing = findOne(id);
        existing.setMoDangKy(mo != null ? mo : !Boolean.TRUE.equals(existing.getMoDangKy()));
        return repository.save(existing);
    }

    @PatchMapping("/{id}/khoa-nam-hoc")
    @PreAuthorize("hasRole('BCN_KHOA')")
    public NamHoc khoaNamHoc(@PathVariable Integer id) {
        HocKy existing = findOne(id);
        NamHoc namHoc = existing.getNamHoc();
        namHoc.setTrangThai(TrangThaiNamHoc.DA_KHOA);
        return namHocRepository.save(namHoc);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.delete(findOne(id));
    }
}

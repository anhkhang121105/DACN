package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.KeHoachMoLop;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.HocKyRepository;
import vn.edu.sgu.phancong.repository.KeHoachMoLopRepository;
import vn.edu.sgu.phancong.repository.MonHocRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;
import vn.edu.sgu.phancong.service.KeHoachMoLopService;

import java.util.List;

@RestController
@RequestMapping("/api/ke-hoach-mo-lop")
@RequiredArgsConstructor
public class KeHoachMoLopController {

    private final KeHoachMoLopRepository repository;
    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final HocKyRepository hocKyRepository;
    private final MonHocRepository monHocRepository;
    private final KeHoachMoLopService service;

    @GetMapping
    public List<KeHoachMoLop> findAll(@RequestParam(required = false) Integer hocKyId) {
        if (hocKyId != null) {
            return repository.findByHocKyId(hocKyId);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public KeHoachMoLop findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Kế hoạch mở lớp", id));
    }

    @GetMapping("/{id}/nhom")
    public List<NhomHocPhan> nhomCuaKeHoach(@PathVariable Integer id) {
        return nhomHocPhanRepository.findByKeHoachIdOrderBySoNhomAscLoaiAsc(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public KeHoachMoLop create(@RequestBody KeHoachMoLop body,
                               @RequestParam(defaultValue = "false") boolean sinhNhom) {
        return service.create(body, sinhNhom);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public KeHoachMoLop update(@PathVariable Integer id, @RequestBody KeHoachMoLop body) {
        KeHoachMoLop existing = findOne(id);
        existing.setKhoaHienThi(body.getKhoaHienThi());
        existing.setTongSv(body.getTongSv());
        existing.setMaxSvNhom(body.getMaxSvNhom());
        existing.setGhiChu(body.getGhiChu());
        if (body.getHocKy() != null && body.getHocKy().getId() != null) {
            existing.setHocKy(hocKyRepository.findById(body.getHocKy().getId())
                    .orElseThrow(() -> new NotFoundException("Học kỳ", body.getHocKy().getId())));
        }
        if (body.getMonHoc() != null && body.getMonHoc().getId() != null) {
            existing.setMonHoc(monHocRepository.findById(body.getMonHoc().getId())
                    .orElseThrow(() -> new NotFoundException("Môn học", body.getMonHoc().getId())));
        }
        return repository.save(existing);
    }

    @PostMapping("/{id}/sinh-nhom")
    @PreAuthorize("hasRole('TRO_LY')")
    public List<NhomHocPhan> sinhNhom(@PathVariable Integer id,
                                      @RequestParam(defaultValue = "false") boolean force) {
        return service.sinhNhom(id, force);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        KeHoachMoLop existing = findOne(id);
        repository.delete(existing);
    }
}

package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.CongTacKhac;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.CongTacKhacRepository;

import java.util.List;

@RestController
@RequestMapping("/api/cong-tac-khac")
@RequiredArgsConstructor
public class CongTacKhacController {

    private final CongTacKhacRepository repository;

    @GetMapping
    public List<CongTacKhac> findAll(@RequestParam(required = false) Integer namHocId,
                                     @RequestParam(required = false) Integer giangVienId) {
        if (giangVienId != null && namHocId != null) {
            return repository.findByGiangVienIdAndNamHocId(giangVienId, namHocId);
        }
        if (namHocId != null) {
            return repository.findByNamHocId(namHocId);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public CongTacKhac findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Công tác khác", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public CongTacKhac create(@RequestBody CongTacKhac body) {
        body.setId(null);
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public CongTacKhac update(@PathVariable Integer id, @RequestBody CongTacKhac body) {
        CongTacKhac existing = findOne(id);
        existing.setGiangVien(body.getGiangVien());
        existing.setNamHoc(body.getNamHoc());
        existing.setLoaiCongTac(body.getLoaiCongTac());
        existing.setSoTiet(body.getSoTiet());
        existing.setLaClc(body.getLaClc());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.delete(findOne(id));
    }
}

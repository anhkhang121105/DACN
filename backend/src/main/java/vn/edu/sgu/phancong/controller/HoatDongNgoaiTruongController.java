package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.HoatDongNgoaiTruong;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.HoatDongNgoaiTruongRepository;

import java.util.List;

@RestController
@RequestMapping("/api/hoat-dong-ngoai-truong")
@RequiredArgsConstructor
public class HoatDongNgoaiTruongController {

    private final HoatDongNgoaiTruongRepository repository;

    @GetMapping
    public List<HoatDongNgoaiTruong> findAll(@RequestParam(required = false) Integer namHocId) {
        if (namHocId != null) {
            return repository.findByNamHocId(namHocId);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public HoatDongNgoaiTruong findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Hoạt động ngoài trường", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public HoatDongNgoaiTruong create(@RequestBody HoatDongNgoaiTruong body) {
        body.setId(null);
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public HoatDongNgoaiTruong update(@PathVariable Integer id, @RequestBody HoatDongNgoaiTruong body) {
        HoatDongNgoaiTruong existing = findOne(id);
        existing.setNamHoc(body.getNamHoc());
        existing.setTrinhDo(body.getTrinhDo());
        existing.setKhoa(body.getKhoa());
        existing.setNoiDung(body.getNoiDung());
        existing.setDiaDiem(body.getDiaDiem());
        existing.setHocKy(body.getHocKy());
        existing.setSoNgay(body.getSoNgay());
        existing.setTuNgay(body.getTuNgay());
        existing.setDenNgay(body.getDenNgay());
        existing.setSoLuongSv(body.getSoLuongSv());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.delete(findOne(id));
    }
}

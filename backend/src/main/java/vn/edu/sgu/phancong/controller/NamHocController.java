package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.NamHoc;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.HocKyRepository;
import vn.edu.sgu.phancong.repository.KeHoachBoMonRepository;
import vn.edu.sgu.phancong.repository.NamHocRepository;

import java.util.List;

@RestController
@RequestMapping("/api/nam-hoc")
@RequiredArgsConstructor
public class NamHocController {

    private final NamHocRepository repository;
    private final HocKyRepository hocKyRepository;
    private final KeHoachBoMonRepository keHoachBoMonRepository;

    @GetMapping
    public List<NamHoc> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public NamHoc findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Năm học", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public NamHoc create(@RequestBody NamHoc body) {
        body.setId(null);
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public NamHoc update(@PathVariable Integer id, @RequestBody NamHoc body) {
        NamHoc existing = findOne(id);
        existing.setTen(body.getTen());
        if (body.getTrangThai() != null) {
            existing.setTrangThai(body.getTrangThai());
        }
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        NamHoc existing = findOne(id);
        if (!hocKyRepository.findByNamHocIdOrderBySoHkAsc(id).isEmpty()) {
            throw new BadRequestException("Không thể xóa năm học đã có học kỳ");
        }
        if (!keHoachBoMonRepository.findByNamHocId(id).isEmpty()) {
            throw new BadRequestException("Không thể xóa năm học đã có kế hoạch bộ môn");
        }
        repository.delete(existing);
    }
}

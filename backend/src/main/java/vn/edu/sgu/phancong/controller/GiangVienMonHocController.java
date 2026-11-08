package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.GiangVienMonHoc;
import vn.edu.sgu.phancong.repository.GiangVienMonHocRepository;

import java.util.List;

@RestController
@RequestMapping("/api/giang-vien-mon-hoc")
@RequiredArgsConstructor
public class GiangVienMonHocController {

    private final GiangVienMonHocRepository repository;

    @GetMapping
    public List<GiangVienMonHoc> findAll(@RequestParam(required = false) Integer giangVienId,
                                         @RequestParam(required = false) Integer monHocId) {
        if (giangVienId != null) {
            return repository.findByGiangVienId(giangVienId);
        }
        if (monHocId != null) {
            return repository.findByMonHocId(monHocId);
        }
        return repository.findAll();
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public GiangVienMonHoc create(@RequestBody GiangVienMonHoc body) {
        if (body.getCoTheDayLt() == null) {
            body.setCoTheDayLt(true);
        }
        if (body.getCoTheDayTh() == null) {
            body.setCoTheDayTh(true);
        }
        return repository.save(body);
    }

    @DeleteMapping("/{giangVienId}/{monHocId}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer giangVienId, @PathVariable Integer monHocId) {
        repository.deleteByGiangVienIdAndMonHocId(giangVienId, monHocId);
    }
}

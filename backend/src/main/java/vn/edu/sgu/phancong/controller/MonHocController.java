package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.BoMon;
import vn.edu.sgu.phancong.entity.KhoiKienThuc;
import vn.edu.sgu.phancong.entity.MonHoc;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.BoMonRepository;
import vn.edu.sgu.phancong.repository.KhoiKienThucRepository;
import vn.edu.sgu.phancong.repository.MonHocRepository;

import java.util.List;

@RestController
@RequestMapping("/api/mon-hoc")
@RequiredArgsConstructor
public class MonHocController {

    private final MonHocRepository repository;
    private final BoMonRepository boMonRepository;
    private final KhoiKienThucRepository khoiKienThucRepository;

    @GetMapping
    public List<MonHoc> findAll(@RequestParam(required = false) Integer boMonId) {
        if (boMonId != null) {
            return repository.findByBoMonId(boMonId);
        }
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public MonHoc findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Môn học", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public MonHoc create(@RequestBody MonHoc body) {
        body.setId(null);
        applyRelations(body);
        if (body.getHeSoHp() == null) {
            body.setHeSoHp(java.math.BigDecimal.ONE);
        }
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public MonHoc update(@PathVariable Integer id, @RequestBody MonHoc body) {
        MonHoc existing = findOne(id);
        existing.setMaHp(body.getMaHp());
        existing.setTen(body.getTen());
        existing.setSoTc(body.getSoTc());
        existing.setTietLt(body.getTietLt());
        existing.setTietBt(body.getTietBt());
        existing.setTietTh(body.getTietTh());
        existing.setHeSoHp(body.getHeSoHp());
        if (body.getBoMon() != null) {
            existing.setBoMon(resolveBoMon(body.getBoMon().getId()));
        }
        existing.setKhoiKienThuc(body.getKhoiKienThuc() != null
                ? khoiKienThucRepository.findById(body.getKhoiKienThuc().getId()).orElse(null) : null);
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.delete(findOne(id));
    }

    private void applyRelations(MonHoc body) {
        if (body.getBoMon() == null || body.getBoMon().getId() == null) {
            throw new BadRequestException("Môn học phải thuộc một bộ môn");
        }
        body.setBoMon(resolveBoMon(body.getBoMon().getId()));
        if (body.getKhoiKienThuc() != null && body.getKhoiKienThuc().getId() != null) {
            body.setKhoiKienThuc(khoiKienThucRepository.findById(body.getKhoiKienThuc().getId()).orElse(null));
        }
    }

    private BoMon resolveBoMon(Integer id) {
        return boMonRepository.findById(id).orElseThrow(() -> new NotFoundException("Bộ môn", id));
    }
}

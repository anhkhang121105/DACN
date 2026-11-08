package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.KhoiKienThuc;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.KhoiKienThucRepository;

import java.util.List;

@RestController
@RequestMapping("/api/khoi-kien-thuc")
@RequiredArgsConstructor
public class KhoiKienThucController {

    private final KhoiKienThucRepository repository;

    @GetMapping
    public List<KhoiKienThuc> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public KhoiKienThuc findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Khối kiến thức", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public KhoiKienThuc create(@RequestBody KhoiKienThuc body) {
        body.setId(null);
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public KhoiKienThuc update(@PathVariable Integer id, @RequestBody KhoiKienThuc body) {
        KhoiKienThuc existing = findOne(id);
        existing.setTen(body.getTen());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.delete(findOne(id));
    }
}

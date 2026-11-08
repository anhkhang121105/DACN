package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.LoaiCongTac;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.LoaiCongTacRepository;

import java.util.List;

@RestController
@RequestMapping("/api/loai-cong-tac")
@RequiredArgsConstructor
public class LoaiCongTacController {

    private final LoaiCongTacRepository repository;

    @GetMapping
    public List<LoaiCongTac> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{ma}")
    public LoaiCongTac findOne(@PathVariable String ma) {
        return repository.findById(ma).orElseThrow(() -> new NotFoundException("Loại công tác", ma));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public LoaiCongTac create(@RequestBody LoaiCongTac body) {
        return repository.save(body);
    }

    @PutMapping("/{ma}")
    @PreAuthorize("hasRole('TRO_LY')")
    public LoaiCongTac update(@PathVariable String ma, @RequestBody LoaiCongTac body) {
        LoaiCongTac existing = findOne(ma);
        existing.setTen(body.getTen());
        existing.setSoTietMacDinh(body.getSoTietMacDinh());
        return repository.save(existing);
    }

    @DeleteMapping("/{ma}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String ma) {
        repository.delete(findOne(ma));
    }
}

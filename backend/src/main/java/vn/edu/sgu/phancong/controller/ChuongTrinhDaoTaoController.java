package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.ChuongTrinhDaoTao;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.ChuongTrinhDaoTaoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/ctdt")
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoController {

    private final ChuongTrinhDaoTaoRepository repository;

    @GetMapping
    public List<ChuongTrinhDaoTao> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ChuongTrinhDaoTao findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("CTĐT", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public ChuongTrinhDaoTao create(@RequestBody ChuongTrinhDaoTao body) {
        body.setId(null);
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public ChuongTrinhDaoTao update(@PathVariable Integer id, @RequestBody ChuongTrinhDaoTao body) {
        ChuongTrinhDaoTao existing = findOne(id);
        existing.setMa(body.getMa());
        existing.setTen(body.getTen());
        existing.setHe(body.getHe());
        existing.setKhoaTuyen(body.getKhoaTuyen());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        repository.delete(findOne(id));
    }
}

package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.BoMon;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.BoMonRepository;
import vn.edu.sgu.phancong.repository.GiangVienRepository;

import java.util.List;

@RestController
@RequestMapping("/api/bo-mon")
@RequiredArgsConstructor
public class BoMonController {

    private final BoMonRepository repository;
    private final GiangVienRepository giangVienRepository;

    @GetMapping
    public List<BoMon> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public BoMon findOne(@PathVariable Integer id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Bộ môn", id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.CREATED)
    public BoMon create(@RequestBody BoMon body) {
        body.setId(null);
        return repository.save(body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    public BoMon update(@PathVariable Integer id, @RequestBody BoMon body) {
        BoMon existing = findOne(id);
        existing.setMa(body.getMa());
        existing.setTen(body.getTen());
        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TRO_LY')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        BoMon existing = findOne(id);
        if (giangVienRepository.countByBoMonId(id) > 0) {
            throw new BadRequestException("Không thể xóa bộ môn đang có giảng viên");
        }
        repository.delete(existing);
    }
}

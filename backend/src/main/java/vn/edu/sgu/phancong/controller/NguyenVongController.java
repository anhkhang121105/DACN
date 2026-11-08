package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.entity.NguyenVong;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNguyenVong;
import vn.edu.sgu.phancong.repository.NguyenVongRepository;
import vn.edu.sgu.phancong.security.SecurityUtils;
import vn.edu.sgu.phancong.service.NguyenVongService;

import java.util.List;

@RestController
@RequestMapping("/api/nguyen-vong")
@RequiredArgsConstructor
public class NguyenVongController {

    private final NguyenVongService service;
    private final NguyenVongRepository repository;

    @GetMapping("/cua-toi")
    @PreAuthorize("hasRole('GIANG_VIEN')")
    public List<NguyenVong> cuaToi(@RequestParam(required = false) Integer hocKyId) {
        return service.cuaToi(SecurityUtils.getCurrentGiangVienId(), hocKyId);
    }

    @PostMapping
    @PreAuthorize("hasRole('GIANG_VIEN')")
    @ResponseStatus(HttpStatus.CREATED)
    public NguyenVong dangKy(@RequestBody NguyenVong body) {
        return service.dangKy(SecurityUtils.getCurrentGiangVienId(), body);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GIANG_VIEN')")
    public NguyenVong capNhat(@PathVariable Integer id, @RequestBody NguyenVong body) {
        return service.capNhat(SecurityUtils.getCurrentGiangVienId(), id, body);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('GIANG_VIEN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void xoa(@PathVariable Integer id) {
        service.xoa(SecurityUtils.getCurrentGiangVienId(), id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TRUONG_BO_MON','BCN_KHOA','TRO_LY')")
    public List<NguyenVong> findAll(@RequestParam(required = false) Integer hocKyId,
                                    @RequestParam(required = false) Integer boMonId) {
        if (hocKyId != null && boMonId != null) {
            return repository.findByHocKyIdAndGiangVienBoMonId(hocKyId, boMonId);
        }
        if (boMonId != null && SecurityUtils.hasRole("TRUONG_BO_MON")) {
            Integer myBoMon = SecurityUtils.getCurrentBoMonId();
            if (myBoMon != null) {
                return repository.findAll().stream()
                        .filter(n -> n.getGiangVien().getBoMon() != null
                                && n.getGiangVien().getBoMon().getId().equals(myBoMon))
                        .toList();
            }
        }
        if (hocKyId != null) {
            return repository.findByHocKyId(hocKyId);
        }
        return repository.findAll();
    }

    @PatchMapping("/{id}/duyet")
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public NguyenVong duyet(@PathVariable Integer id, @RequestParam TrangThaiNguyenVong trangThai) {
        return service.duyet(id, trangThai);
    }
}

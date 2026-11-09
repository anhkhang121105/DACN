package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.dto.LichRanhBanBulkRequest;
import vn.edu.sgu.phancong.entity.LichRanhBan;
import vn.edu.sgu.phancong.security.SecurityUtils;
import vn.edu.sgu.phancong.service.LichRanhBanService;

import java.util.List;

@RestController
@RequestMapping("/api/lich-ranh-ban")
@RequiredArgsConstructor
public class LichRanhBanController {

    private final LichRanhBanService service;

    @GetMapping("/cua-toi")
    @PreAuthorize("hasRole('GIANG_VIEN')")
    public List<LichRanhBan> cuaToi(@RequestParam Integer hocKyId) {
        return service.get(SecurityUtils.getCurrentGiangVienId(), hocKyId);
    }

    @PutMapping("/cua-toi")
    @PreAuthorize("hasRole('GIANG_VIEN')")
    public List<LichRanhBan> capNhat(@RequestBody LichRanhBanBulkRequest request) {
        return service.upsert(SecurityUtils.getCurrentGiangVienId(), request);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TRUONG_BO_MON','BCN_KHOA','TRO_LY')")
    public List<LichRanhBan> findAll(@RequestParam Integer giangVienId, @RequestParam Integer hocKyId) {
        return service.get(giangVienId, hocKyId);
    }
}

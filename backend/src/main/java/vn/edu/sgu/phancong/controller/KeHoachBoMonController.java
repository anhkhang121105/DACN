package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.dto.CsrResult;
import vn.edu.sgu.phancong.dto.KeHoachBoMonRequest;
import vn.edu.sgu.phancong.dto.ReturnRequest;
import vn.edu.sgu.phancong.entity.KeHoachBoMon;
import vn.edu.sgu.phancong.security.SecurityUtils;
import vn.edu.sgu.phancong.service.KeHoachBoMonService;

import java.util.List;

@RestController
@RequestMapping("/api/ke-hoach-bo-mon")
@RequiredArgsConstructor
public class KeHoachBoMonController {

    private final KeHoachBoMonService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('TRUONG_BO_MON','BCN_KHOA','TRO_LY')")
    public List<KeHoachBoMon> findAll(@RequestParam(required = false) Integer namHocId,
                                      @RequestParam(required = false) Integer boMonId) {
        Integer filterBoMon = boMonId;
        if (SecurityUtils.hasRole("TRUONG_BO_MON")) {
            filterBoMon = SecurityUtils.getCurrentBoMonId();
        }
        return service.findAll(namHocId, filterBoMon);
    }

    @PostMapping
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public KeHoachBoMon tao(@RequestBody KeHoachBoMonRequest request) {
        Integer boMonId = request.getBoMonId() != null ? request.getBoMonId() : SecurityUtils.getCurrentBoMonId();
        return service.taoHoacLay(request.getNamHocId(), boMonId);
    }

    @GetMapping("/{id}/csr")
    @PreAuthorize("hasAnyRole('TRUONG_BO_MON','BCN_KHOA')")
    public CsrResult csr(@PathVariable Integer id) {
        return service.csrHienTai(id);
    }

    @PatchMapping("/{id}/submit")
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public KeHoachBoMon nop(@PathVariable Integer id) {
        return service.nop(id);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('BCN_KHOA')")
    public KeHoachBoMon duyet(@PathVariable Integer id) {
        return service.duyet(id);
    }

    @PatchMapping("/{id}/return")
    @PreAuthorize("hasRole('BCN_KHOA')")
    public KeHoachBoMon traVe(@PathVariable Integer id, @RequestBody ReturnRequest request) {
        return service.traVe(id, request.getLyDo());
    }
}

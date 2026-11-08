package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.dto.CsrResult;
import vn.edu.sgu.phancong.dto.phancong.PhanCongRequest;
import vn.edu.sgu.phancong.dto.phancong.PhanCongResult;
import vn.edu.sgu.phancong.dto.phancong.SolverAssignment;
import vn.edu.sgu.phancong.dto.phancong.SolverResult;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.rules.RuleViolation;
import vn.edu.sgu.phancong.security.SecurityUtils;
import vn.edu.sgu.phancong.service.CsrService;
import vn.edu.sgu.phancong.service.PhanCongService;

import java.util.List;

@RestController
@RequestMapping("/api/phan-cong")
@RequiredArgsConstructor
public class PhanCongController {

    private final PhanCongService service;
    private final CsrService csrService;

    @GetMapping("/csr")
    @PreAuthorize("hasAnyRole('TRUONG_BO_MON','BCN_KHOA','TRO_LY')")
    public CsrResult csr(@RequestParam Integer namHocId,
                         @RequestParam(required = false) Integer boMonId) {
        Integer filter = boMonId != null ? boMonId : SecurityUtils.getCurrentBoMonId();
        if (filter == null) {
            throw new vn.edu.sgu.phancong.exception.BadRequestException("Không xác định được bộ môn");
        }
        return csrService.tinhCsr(filter, namHocId);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('TRUONG_BO_MON','BCN_KHOA','TRO_LY')")
    public List<NhomHocPhan> theoHocKy(@RequestParam Integer hocKyId,
                                       @RequestParam(required = false) Integer boMonId) {
        Integer filterBoMon = boMonId;
        if (SecurityUtils.hasRole("TRUONG_BO_MON") && filterBoMon == null) {
            filterBoMon = SecurityUtils.getCurrentBoMonId();
        }
        return service.theoHocKy(hocKyId, filterBoMon);
    }

    @PostMapping("/thu-cong")
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public PhanCongResult gan(@RequestParam Integer nhomId, @RequestBody(required = false) PhanCongRequest body,
                              @RequestParam(required = false) Integer giangVienId) {
        Integer gvId = body != null && body.getGiangVienId() != null ? body.getGiangVienId() : giangVienId;
        if (gvId == null) {
            throw new vn.edu.sgu.phancong.exception.BadRequestException("giangVienId không được để trống");
        }
        Boolean isFixed = body != null ? body.getIsFixed() : null;
        return service.gan(nhomId, gvId, isFixed);
    }

    @DeleteMapping("/thu-cong/{nhomId}")
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public PhanCongResult huyGan(@PathVariable Integer nhomId) {
        return service.huyGan(nhomId);
    }

    @PatchMapping("/{nhomId}/lich-day")
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public NhomHocPhan datLich(@PathVariable Integer nhomId,
                               @RequestParam(required = false) Integer thu,
                               @RequestParam(required = false) Integer ca) {
        return service.datLich(nhomId, thu, ca);
    }

    @GetMapping("/{nhomId}/kiem-tra")
    @PreAuthorize("hasAnyRole('TRUONG_BO_MON','BCN_KHOA')")
    public List<RuleViolation> kiemTra(@PathVariable Integer nhomId) {
        return service.kiemTraNhom(nhomId);
    }

    @PostMapping("/goi-y")
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public SolverResult goiY(@RequestParam Integer hocKyId,
                             @RequestParam(required = false) Integer boMonId) {
        Integer filterBoMon = boMonId != null ? boMonId : SecurityUtils.getCurrentBoMonId();
        if (filterBoMon == null) {
            throw new vn.edu.sgu.phancong.exception.BadRequestException("Không xác định được bộ môn");
        }
        return service.goiY(filterBoMon, hocKyId);
    }

    @PostMapping("/goi-y/chap-nhan")
    @PreAuthorize("hasRole('TRUONG_BO_MON')")
    public List<NhomHocPhan> chapNhan(@RequestBody List<SolverAssignment> assignments) {
        return service.chapNhanGoiY(assignments);
    }
}

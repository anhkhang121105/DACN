package vn.edu.sgu.phancong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import vn.edu.sgu.phancong.dto.report.TaiLuongDto;
import vn.edu.sgu.phancong.report.ReportService;
import vn.edu.sgu.phancong.security.SecurityUtils;
import vn.edu.sgu.phancong.service.WorkloadService;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/bao-cao")
@RequiredArgsConstructor
public class BaoCaoController {

    private static final MediaType XLSX = MediaType.parseMediaType(
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final ReportService reportService;
    private final WorkloadService workloadService;

    @GetMapping("/mau-1")
    @PreAuthorize("hasAnyRole('BCN_KHOA','TRUONG_BO_MON','TRO_LY')")
    public ResponseEntity<byte[]> mau1(@RequestParam Integer hocKyId,
                                       @RequestParam(required = false) Integer boMonId) {
        return excel(reportService.xuatMau1(hocKyId, boMonId), "Mau1_PhanCong.xlsx");
    }

    @GetMapping("/mau-2")
    @PreAuthorize("hasAnyRole('BCN_KHOA','TRO_LY')")
    public ResponseEntity<byte[]> mau2(@RequestParam Integer namHocId) {
        return excel(reportService.xuatMau2(namHocId), "Mau2_TongHopTaiLuong.xlsx");
    }

    @GetMapping("/mau-3")
    @PreAuthorize("hasAnyRole('BCN_KHOA','TRO_LY')")
    public ResponseEntity<byte[]> mau3(@RequestParam Integer namHocId) {
        return excel(reportService.xuatMau3(namHocId), "Mau3_MoiGiang.xlsx");
    }

    @GetMapping("/mau-4")
    @PreAuthorize("hasAnyRole('BCN_KHOA','TRO_LY')")
    public ResponseEntity<byte[]> mau4(@RequestParam Integer namHocId) {
        return excel(reportService.xuatMau4(namHocId), "Mau4_ThinhGiang.xlsx");
    }

    @GetMapping("/mau-5")
    @PreAuthorize("hasAnyRole('BCN_KHOA','TRO_LY')")
    public ResponseEntity<byte[]> mau5(@RequestParam Integer namHocId) {
        return excel(reportService.xuatMau5(namHocId), "Mau5_HoatDong.xlsx");
    }

    @GetMapping("/tai-luong")
    @PreAuthorize("hasAnyRole('BCN_KHOA','TRUONG_BO_MON','TRO_LY')")
    public List<TaiLuongDto> taiLuong(@RequestParam Integer namHocId,
                                      @RequestParam(required = false) Integer boMonId) {
        Integer filter = boMonId;
        if (SecurityUtils.hasRole("TRUONG_BO_MON")) {
            filter = SecurityUtils.getCurrentBoMonId();
        }
        if (filter != null) {
            return workloadService.taiLuongBoMon(filter, namHocId);
        }
        return workloadService.taiLuongKhoa(namHocId);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('BCN_KHOA')")
    public List<TaiLuongDto> dashboard(@RequestParam Integer namHocId) {
        return workloadService.taiLuongKhoa(namHocId);
    }

    private ResponseEntity<byte[]> excel(byte[] data, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(XLSX);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(filename, StandardCharsets.UTF_8)
                .build());
        headers.setContentLength(data.length);
        return ResponseEntity.ok().headers(headers).body(data);
    }
}

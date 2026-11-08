package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.dto.phancong.PhanCongResult;
import vn.edu.sgu.phancong.dto.phancong.SolverAssignment;
import vn.edu.sgu.phancong.dto.phancong.SolverResult;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.entity.enums.NguonPhanCong;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.ForbiddenException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.GiangVienRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleContextLoader;
import vn.edu.sgu.phancong.rules.RuleViolation;
import vn.edu.sgu.phancong.rules.RulesEngine;
import vn.edu.sgu.phancong.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PhanCongService {

    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final GiangVienRepository giangVienRepository;
    private final RuleContextLoader contextLoader;
    private final RulesEngine rulesEngine;
    private final CsrService csrService;
    private final SolverService solverService;

    public List<NhomHocPhan> theoHocKy(Integer hocKyId, Integer boMonId) {
        return nhomHocPhanRepository.findByKeHoachHocKyId(hocKyId).stream()
                .filter(n -> boMonId == null
                        || (n.getKeHoach().getMonHoc().getBoMon() != null
                        && n.getKeHoach().getMonHoc().getBoMon().getId().equals(boMonId)))
                .toList();
    }

    @Transactional
    public PhanCongResult gan(Integer nhomId, Integer giangVienId, Boolean isFixed) {
        NhomHocPhan nhom = nhomHocPhanRepository.findById(nhomId)
                .orElseThrow(() -> new NotFoundException("Nhóm học phần", nhomId));
        GiangVien gv = giangVienRepository.findById(giangVienId)
                .orElseThrow(() -> new NotFoundException("Giảng viên", giangVienId));

        kiemTraQuyen(nhom);

        Integer namHocId = nhom.getKeHoach().getHocKy().getNamHoc().getId();
        RuleContext context = contextLoader.loadForNamHoc(namHocId);
        List<RuleViolation> violations = rulesEngine.evaluateAssignment(context, nhom, gv);
        List<RuleViolation> hard = violations.stream().filter(RuleViolation::isCung).toList();
        if (!hard.isEmpty()) {
            String message = hard.stream().map(RuleViolation::getMoTa).reduce((a, b) -> a + "; " + b).orElse("");
            throw new BadRequestException("Vi phạm luật cứng: " + message);
        }

        nhom.setGiangVien(gv);
        nhom.setNguon(NguonPhanCong.THU_CONG);
        nhom.setIsFixed(isFixed == null || isFixed);
        NhomHocPhan saved = nhomHocPhanRepository.save(nhom);
        capNhatCsr(nhom);

        return PhanCongResult.builder()
                .nhom(saved)
                .violations(violations)
                .thanhCong(true)
                .build();
    }

    @Transactional
    public PhanCongResult huyGan(Integer nhomId) {
        NhomHocPhan nhom = nhomHocPhanRepository.findById(nhomId)
                .orElseThrow(() -> new NotFoundException("Nhóm học phần", nhomId));
        kiemTraQuyen(nhom);
        if (nhom.getGiangVien() == null) {
            throw new BadRequestException("Nhóm chưa được phân công");
        }
        nhom.setGiangVien(null);
        nhom.setNguon(NguonPhanCong.CHUA);
        nhom.setIsFixed(false);
        NhomHocPhan saved = nhomHocPhanRepository.save(nhom);
        capNhatCsr(nhom);
        return PhanCongResult.builder().nhom(saved).violations(List.of()).thanhCong(true).build();
    }

    @Transactional
    public NhomHocPhan datLich(Integer nhomId, Integer thu, Integer ca) {
        NhomHocPhan nhom = nhomHocPhanRepository.findById(nhomId)
                .orElseThrow(() -> new NotFoundException("Nhóm học phần", nhomId));
        kiemTraQuyen(nhom);
        if (thu != null && (thu < 2 || thu > 7)) {
            throw new BadRequestException("Thứ phải nằm trong khoảng 2..7");
        }
        if (ca != null && (ca < 1 || ca > 4)) {
            throw new BadRequestException("Ca phải nằm trong khoảng 1..4");
        }
        nhom.setThu(thu);
        nhom.setCa(ca);
        return nhomHocPhanRepository.save(nhom);
    }

    public List<RuleViolation> kiemTraNhom(Integer nhomId) {
        NhomHocPhan nhom = nhomHocPhanRepository.findById(nhomId)
                .orElseThrow(() -> new NotFoundException("Nhóm học phần", nhomId));
        RuleContext context = contextLoader.loadForNamHoc(nhom.getKeHoach().getHocKy().getNamHoc().getId());
        return rulesEngine.evaluateNhom(context, nhom);
    }

    public SolverResult goiY(Integer boMonId, Integer hocKyId) {
        return solverService.solve(boMonId, hocKyId);
    }

    @Transactional
    public List<NhomHocPhan> chapNhanGoiY(List<SolverAssignment> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            throw new BadRequestException("Không có kết quả gợi ý để áp dụng");
        }
        List<NhomHocPhan> updated = new ArrayList<>();
        for (SolverAssignment a : assignments) {
            NhomHocPhan nhom = nhomHocPhanRepository.findById(a.getNhomId())
                    .orElseThrow(() -> new NotFoundException("Nhóm học phần", a.getNhomId()));
            kiemTraQuyen(nhom);
            GiangVien gv = giangVienRepository.findById(a.getGiangVienId())
                    .orElseThrow(() -> new NotFoundException("Giảng viên", a.getGiangVienId()));
            nhom.setGiangVien(gv);
            nhom.setNguon(NguonPhanCong.CP_SAT);
            nhom.setIsFixed(false);
            updated.add(nhomHocPhanRepository.save(nhom));
        }
        if (!updated.isEmpty()) {
            capNhatCsr(updated.get(0));
        }
        return updated;
    }

    private void kiemTraQuyen(NhomHocPhan nhom) {
        if (!SecurityUtils.hasRole("TRUONG_BO_MON")) {
            return;
        }
        Integer myBoMon = SecurityUtils.getCurrentBoMonId();
        Integer nhomBoMon = nhom.getKeHoach().getMonHoc().getBoMon() != null
                ? nhom.getKeHoach().getMonHoc().getBoMon().getId() : null;
        if (myBoMon == null || nhomBoMon == null || !myBoMon.equals(nhomBoMon)) {
            throw new ForbiddenException("Chỉ được thao tác trên nhóm thuộc bộ môn mình");
        }
    }

    private void capNhatCsr(NhomHocPhan nhom) {
        Integer boMonId = nhom.getKeHoach().getMonHoc().getBoMon() != null
                ? nhom.getKeHoach().getMonHoc().getBoMon().getId() : null;
        Integer namHocId = nhom.getKeHoach().getHocKy().getNamHoc().getId();
        if (boMonId != null) {
            csrService.capNhatKeHoach(boMonId, namHocId);
        }
    }
}

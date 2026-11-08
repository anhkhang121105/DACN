package vn.edu.sgu.phancong.service;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.sgu.phancong.dto.phancong.SolverAssignment;
import vn.edu.sgu.phancong.dto.phancong.SolverResult;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.HocKy;
import vn.edu.sgu.phancong.entity.LichRanhBan;
import vn.edu.sgu.phancong.entity.NguyenVong;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.entity.enums.LoaiNhom;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNguyenVong;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.GiangVienMonHocRepository;
import vn.edu.sgu.phancong.repository.GiangVienRepository;
import vn.edu.sgu.phancong.repository.HocKyRepository;
import vn.edu.sgu.phancong.repository.LichRanhBanRepository;
import vn.edu.sgu.phancong.repository.NguyenVongRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SolverService {

    private static final int SCALE = 100;
    private static final int PREF_WEIGHT = 1000;
    private static final int BALANCE_WEIGHT = 10;

    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final GiangVienRepository giangVienRepository;
    private final GiangVienMonHocRepository giangVienMonHocRepository;
    private final LichRanhBanRepository lichRanhBanRepository;
    private final NguyenVongRepository nguyenVongRepository;
    private final HocKyRepository hocKyRepository;

    public SolverResult solve(Integer boMonId, Integer hocKyId) {
        Loader.loadNativeLibraries();

        HocKy hocKy = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new NotFoundException("Học kỳ", hocKyId));
        Integer namHocId = hocKy.getNamHoc().getId();

        List<NhomHocPhan> nhomCuaBoMon = nhomHocPhanRepository.findByKeHoachHocKyId(hocKyId).stream()
                .filter(n -> n.getKeHoach().getMonHoc().getBoMon() != null
                        && n.getKeHoach().getMonHoc().getBoMon().getId().equals(boMonId))
                .toList();

        List<NhomHocPhan> freeNhom = nhomCuaBoMon.stream()
                .filter(n -> n.getGiangVien() == null)
                .toList();

        if (freeNhom.isEmpty()) {
            return SolverResult.builder()
                    .status("OPTIMAL")
                    .message("Không có nhóm nào cần phân công")
                    .objectiveValue(0)
                    .assignments(List.of())
                    .build();
        }

        List<NhomHocPhan> allYear = nhomHocPhanRepository.findByKeHoachHocKyNamHocId(namHocId);
        Set<Integer> freeIds = new HashSet<>();
        freeNhom.forEach(n -> freeIds.add(n.getId()));

        Map<Integer, Integer> baseLoad = new HashMap<>();
        for (NhomHocPhan n : allYear) {
            if (n.getGiangVien() == null || freeIds.contains(n.getId())) {
                continue;
            }
            baseLoad.merge(n.getGiangVien().getId(), toScaled(n.tinhTietThucTe()), Integer::sum);
        }

        List<GiangVien> gvList = giangVienRepository.findAll();
        Map<Integer, GiangVien> gvById = new HashMap<>();
        gvList.forEach(gv -> gvById.put(gv.getId(), gv));

        Set<String> busy = new HashSet<>();
        for (LichRanhBan lrb : lichRanhBanRepository.findByHocKyId(hocKyId)) {
            if (!Boolean.TRUE.equals(lrb.getRanh())) {
                busy.add(lrb.getGiangVienId() + "-" + lrb.getThu() + "-" + lrb.getCa());
            }
        }

        Map<String, Integer> preference = new HashMap<>();
        for (NguyenVong nv : nguyenVongRepository.findByHocKyIdAndTrangThai(hocKyId, TrangThaiNguyenVong.DA_DUYET)) {
            preference.put(nv.getGiangVien().getId() + ":" + nv.getMonHoc().getId(), nv.getMucUuTien());
        }

        Set<String> chuyenMon = new HashSet<>();
        giangVienMonHocRepository.findAll().forEach(g ->
                chuyenMon.add(g.getGiangVienId() + ":" + g.getMonHocId() + ":" + g.getCoTheDayLt() + ":"
                        + g.getCoTheDayTh()));

        CpModel model = new CpModel();
        int soGv = gvList.size();
        int soNhom = freeNhom.size();

        Map<String, IntVar> x = new HashMap<>();
        List<int[]> eligible = new ArrayList<>();
        List<List<Integer>> eligibleGvByNhom = new ArrayList<>();

        for (int j = 0; j < soNhom; j++) {
            NhomHocPhan nhom = freeNhom.get(j);
            List<Integer> okay = new ArrayList<>();
            for (int i = 0; i < soGv; i++) {
                GiangVien gv = gvList.get(i);
                if (!canTeach(chuyenMon, gv.getId(), nhom)) {
                    continue;
                }
                if (nhom.getThu() != null && nhom.getCa() != null
                        && busy.contains(gv.getId() + "-" + nhom.getThu() + "-" + nhom.getCa())) {
                    continue;
                }
                IntVar var = model.newBoolVar("x_" + i + "_" + j);
                x.put(i + ":" + j, var);
                okay.add(i);
            }
            if (okay.isEmpty()) {
                return SolverResult.builder()
                        .status("INFEASIBLE")
                        .message("Không có giảng viên hợp lệ cho nhóm " + nhom.getSoNhom()
                                + " môn " + nhom.getKeHoach().getMonHoc().getTen())
                        .assignments(List.of())
                        .build();
            }
            eligibleGvByNhom.add(okay);
            LinearArgument[] vars = new LinearArgument[okay.size()];
            for (int k = 0; k < okay.size(); k++) {
                vars[k] = x.get(okay.get(k) + ":" + j);
            }
            model.addEquality(LinearExpr.sum(vars), 1);
        }

        int maxTran = 0;
        for (GiangVien gv : gvList) {
            if (gv.getDinhMucMax() != null) {
                maxTran = Math.max(maxTran, toScaled(gv.getDinhMucMax()));
            }
        }
        maxTran = Math.max(maxTran, 400 * SCALE);

        IntVar[] load = new IntVar[soGv];
        for (int i = 0; i < soGv; i++) {
            List<LinearExpr> terms = new ArrayList<>();
            for (int j = 0; j < soNhom; j++) {
                IntVar var = x.get(i + ":" + j);
                if (var != null) {
                    int scaled = toScaled(freeNhom.get(j).tinhTietThucTe());
                    terms.add(LinearExpr.term(var, scaled));
                }
            }
            int base = baseLoad.getOrDefault(gvList.get(i).getId(), 0);
            List<LinearExpr> allTerms = new ArrayList<>(terms);
            allTerms.add(LinearExpr.constant(base));
            load[i] = model.newIntVar(0, maxTran + 100000, "load_" + i);
            model.addEquality(load[i], LinearExpr.sum(allTerms.toArray(LinearExpr[]::new)));
            long tran = gvList.get(i).getDinhMucMax() != null ? toScaled(gvList.get(i).getDinhMucMax()) : 350L * SCALE;
            model.addLessOrEqual(load[i], tran);
        }

        // Ràng buộc trùng ca: các nhóm cùng thứ-ca không cùng một GV
        for (int j1 = 0; j1 < soNhom; j1++) {
            NhomHocPhan n1 = freeNhom.get(j1);
            if (n1.getThu() == null || n1.getCa() == null) {
                continue;
            }
            for (int j2 = j1 + 1; j2 < soNhom; j2++) {
                NhomHocPhan n2 = freeNhom.get(j2);
                if (n2.getThu() == null || n2.getCa() == null) {
                    continue;
                }
                if (!n1.getThu().equals(n2.getThu()) || !n1.getCa().equals(n2.getCa())) {
                    continue;
                }
                for (int i = 0; i < soGv; i++) {
                    IntVar v1 = x.get(i + ":" + j1);
                    IntVar v2 = x.get(i + ":" + j2);
                    if (v1 != null && v2 != null) {
                        model.addLessOrEqual(LinearExpr.sum(new LinearArgument[]{v1, v2}), 1);
                    }
                }
            }
        }

        IntVar maxLoad = model.newIntVar(0, maxTran + 100000, "maxLoad");
        IntVar minLoad = model.newIntVar(0, maxTran + 100000, "minLoad");
        for (int i = 0; i < soGv; i++) {
            model.addLessOrEqual(load[i], maxLoad);
            model.addGreaterOrEqual(load[i], minLoad);
        }

        List<LinearExpr> objectiveTerms = new ArrayList<>();
        for (int j = 0; j < soNhom; j++) {
            NhomHocPhan nhom = freeNhom.get(j);
            Integer monId = nhom.getKeHoach().getMonHoc().getId();
            for (int i : eligibleGvByNhom.get(j)) {
                int pref = preference.getOrDefault(gvList.get(i).getId() + ":" + monId, 0);
                if (pref != 0) {
                    objectiveTerms.add(LinearExpr.term(x.get(i + ":" + j), pref * PREF_WEIGHT));
                }
            }
        }
        objectiveTerms.add(LinearExpr.term(maxLoad, -BALANCE_WEIGHT));
        objectiveTerms.add(LinearExpr.term(minLoad, BALANCE_WEIGHT));
        model.maximize(LinearExpr.sum(objectiveTerms.toArray(LinearExpr[]::new)));

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(5.0);
        solver.getParameters().setNumSearchWorkers(4);
        CpSolverStatus status = solver.solve(model);

        if (status != CpSolverStatus.OPTIMAL && status != CpSolverStatus.FEASIBLE) {
            return SolverResult.builder()
                    .status(status.name())
                    .message("Không tìm được phương án phân công hợp lệ")
                    .assignments(List.of())
                    .build();
        }

        List<SolverAssignment> assignments = new ArrayList<>();
        for (int j = 0; j < soNhom; j++) {
            NhomHocPhan nhom = freeNhom.get(j);
            for (int i : eligibleGvByNhom.get(j)) {
                if (solver.value(x.get(i + ":" + j)) == 1) {
                    GiangVien gv = gvList.get(i);
                    Integer monId = nhom.getKeHoach().getMonHoc().getId();
                    assignments.add(SolverAssignment.builder()
                            .nhomId(nhom.getId())
                            .soNhom(nhom.getSoNhom())
                            .loai(nhom.getLoai().name())
                            .monHoc(nhom.getKeHoach().getMonHoc().getTen())
                            .soTietThucTe(nhom.tinhTietThucTe())
                            .giangVienId(gv.getId())
                            .giangVienTen(gv.getHoTen())
                            .diemUuTien(preference.getOrDefault(gv.getId() + ":" + monId, 0))
                            .build());
                    break;
                }
            }
        }

        return SolverResult.builder()
                .status(status.name())
                .message("Tìm được phương án phân công")
                .objectiveValue(solver.objectiveValue())
                .assignments(assignments)
                .build();
    }

    private boolean canTeach(Set<String> chuyenMon, Integer gvId, NhomHocPhan nhom) {
        String monId = String.valueOf(nhom.getKeHoach().getMonHoc().getId());
        String ltKey = gvId + ":" + monId + ":true:true";
        String ltOnly = gvId + ":" + monId + ":true:false";
        String thOnly = gvId + ":" + monId + ":false:true";
        if (nhom.getLoai() == LoaiNhom.TH) {
            return chuyenMon.contains(ltKey) || chuyenMon.contains(thOnly);
        }
        return chuyenMon.contains(ltKey) || chuyenMon.contains(ltOnly);
    }

    private int toScaled(BigDecimal value) {
        return value == null ? 0 : value.multiply(BigDecimal.valueOf(SCALE)).intValue();
    }
}

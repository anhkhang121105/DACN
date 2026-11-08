package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.dto.CsrResult;
import vn.edu.sgu.phancong.entity.KeHoachBoMon;
import vn.edu.sgu.phancong.entity.NhomHocPhan;
import vn.edu.sgu.phancong.repository.KeHoachBoMonRepository;
import vn.edu.sgu.phancong.repository.NhomHocPhanRepository;
import vn.edu.sgu.phancong.rules.RuleContext;
import vn.edu.sgu.phancong.rules.RuleContextLoader;
import vn.edu.sgu.phancong.rules.RulesEngine;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CsrService {

    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final KeHoachBoMonRepository keHoachBoMonRepository;
    private final RuleContextLoader contextLoader;
    private final RulesEngine rulesEngine;

    public CsrResult tinhCsr(Integer boMonId, Integer namHocId) {
        List<NhomHocPhan> tatCa = nhomHocPhanRepository.findByKeHoachHocKyNamHocId(namHocId);
        List<NhomHocPhan> cuaBoMon = tatCa.stream()
                .filter(n -> n.getKeHoach().getMonHoc().getBoMon() != null
                        && n.getKeHoach().getMonHoc().getBoMon().getId().equals(boMonId))
                .toList();
        RuleContext context = contextLoader.build(namHocId, tatCa);
        return rulesEngine.computeCsr(context, cuaBoMon);
    }

    @Transactional
    public CsrResult capNhatKeHoach(Integer boMonId, Integer namHocId) {
        CsrResult result = tinhCsr(boMonId, namHocId);
        KeHoachBoMon keHoach = keHoachBoMonRepository.findByNamHocIdAndBoMonId(namHocId, boMonId)
                .orElse(null);
        if (keHoach != null) {
            keHoach.setCsr(result.getCsr());
            keHoachBoMonRepository.save(keHoach);
        }
        return result;
    }
}

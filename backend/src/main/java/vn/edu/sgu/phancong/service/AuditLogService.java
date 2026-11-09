package vn.edu.sgu.phancong.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.entity.AuditLog;
import vn.edu.sgu.phancong.repository.AuditLogRepository;
import vn.edu.sgu.phancong.security.SecurityUtils;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public void ghi(String hanhDong, String tenBangTacDong, Integer banGhiId,
                    Object giaTriCu, Object giaTriMoi) {
        Integer nguoiThucHien = SecurityUtils.getCurrentUserId();
        String oldValue = toJson(giaTriCu);
        String newValue = toJson(giaTriMoi);

        Map<String, Object> chiTiet = new LinkedHashMap<>();
        chiTiet.put("nguoi_thuc_hien", nguoiThucHien);
        chiTiet.put("ten_bang_tac_dong", tenBangTacDong);
        chiTiet.put("ban_ghi_id", banGhiId);
        chiTiet.put("gia_tri_cu", giaTriCu);
        chiTiet.put("gia_tri_moi", giaTriMoi);

        auditLogRepository.save(AuditLog.builder()
                .nguoiDungId(nguoiThucHien)
                .nguoiThucHien(nguoiThucHien)
                .hanhDong(hanhDong)
                .doiTuong(tenBangTacDong)
                .tenBangTacDong(tenBangTacDong)
                .doiTuongId(banGhiId)
                .banGhiId(banGhiId)
                .giaTriCu(oldValue)
                .giaTriMoi(newValue)
                .chiTiet(toJson(chiTiet))
                .build());
    }

    private String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Không thể serialize dữ liệu audit log", e);
        }
    }
}

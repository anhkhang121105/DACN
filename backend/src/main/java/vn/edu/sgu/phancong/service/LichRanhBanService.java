package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.dto.LichRanhBanBulkRequest;
import vn.edu.sgu.phancong.entity.LichRanhBan;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.repository.LichRanhBanRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LichRanhBanService {

    private final LichRanhBanRepository repository;

    public List<LichRanhBan> get(Integer giangVienId, Integer hocKyId) {
        return repository.findByGiangVienIdAndHocKyId(giangVienId, hocKyId);
    }

    @Transactional
    public List<LichRanhBan> upsert(Integer giangVienId, LichRanhBanBulkRequest request) {
        if (request.getHocKyId() == null) {
            throw new BadRequestException("hocKyId không được để trống");
        }
        repository.deleteByGiangVienIdAndHocKyId(giangVienId, request.getHocKyId());
        if (request.getItems() == null) {
            return List.of();
        }
        List<LichRanhBan> entities = new ArrayList<>();
        for (LichRanhBanBulkRequest.Item item : request.getItems()) {
            if (item.getThu() == null || item.getThu() < 2 || item.getThu() > 7) {
                throw new BadRequestException("Thứ phải nằm trong khoảng 2..7");
            }
            if (item.getCa() == null || item.getCa() < 1 || item.getCa() > 4) {
                throw new BadRequestException("Ca phải nằm trong khoảng 1..4");
            }
            entities.add(LichRanhBan.builder()
                    .giangVienId(giangVienId)
                    .hocKyId(request.getHocKyId())
                    .thu(item.getThu())
                    .ca(item.getCa())
                    .ranh(item.getRanh() == null || item.getRanh())
                    .build());
        }
        return repository.saveAll(entities);
    }
}

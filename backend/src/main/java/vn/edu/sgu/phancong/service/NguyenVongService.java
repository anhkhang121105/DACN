package vn.edu.sgu.phancong.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.sgu.phancong.entity.GiangVien;
import vn.edu.sgu.phancong.entity.HocKy;
import vn.edu.sgu.phancong.entity.MonHoc;
import vn.edu.sgu.phancong.entity.NguyenVong;
import vn.edu.sgu.phancong.entity.enums.TrangThaiNguyenVong;
import vn.edu.sgu.phancong.exception.BadRequestException;
import vn.edu.sgu.phancong.exception.ForbiddenException;
import vn.edu.sgu.phancong.exception.NotFoundException;
import vn.edu.sgu.phancong.repository.GiangVienMonHocRepository;
import vn.edu.sgu.phancong.repository.GiangVienRepository;
import vn.edu.sgu.phancong.repository.HocKyRepository;
import vn.edu.sgu.phancong.repository.MonHocRepository;
import vn.edu.sgu.phancong.repository.NguyenVongRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NguyenVongService {

    private final NguyenVongRepository repository;
    private final GiangVienRepository giangVienRepository;
    private final HocKyRepository hocKyRepository;
    private final MonHocRepository monHocRepository;
    private final GiangVienMonHocRepository giangVienMonHocRepository;

    public List<NguyenVong> cuaToi(Integer giangVienId, Integer hocKyId) {
        if (hocKyId != null) {
            return repository.findByGiangVienIdAndHocKyId(giangVienId, hocKyId);
        }
        return repository.findAll().stream()
                .filter(n -> n.getGiangVien().getId().equals(giangVienId))
                .toList();
    }

    @Transactional
    public NguyenVong dangKy(Integer giangVienId, NguyenVong body) {
        if (body.getHocKy() == null || body.getHocKy().getId() == null) {
            throw new BadRequestException("Học kỳ không được để trống");
        }
        if (body.getMonHoc() == null || body.getMonHoc().getId() == null) {
            throw new BadRequestException("Môn học không được để trống");
        }
        HocKy hocKy = hocKyRepository.findById(body.getHocKy().getId())
                .orElseThrow(() -> new NotFoundException("Học kỳ", body.getHocKy().getId()));
        if (!Boolean.TRUE.equals(hocKy.getMoDangKy())) {
            throw new BadRequestException("Đợt đăng ký nguyện vọng của học kỳ này chưa mở");
        }
        MonHoc monHoc = monHocRepository.findById(body.getMonHoc().getId())
                .orElseThrow(() -> new NotFoundException("Môn học", body.getMonHoc().getId()));
        if (!giangVienMonHocRepository.existsByGiangVienIdAndMonHocId(giangVienId, monHoc.getId())) {
            throw new BadRequestException("Giảng viên không có chuyên môn với môn học này");
        }
        if (repository.existsByGiangVienIdAndHocKyIdAndMonHocId(giangVienId, hocKy.getId(), monHoc.getId())) {
            throw new BadRequestException("Nguyện vọng cho môn này đã tồn tại");
        }
        GiangVien gv = giangVienRepository.findById(giangVienId)
                .orElseThrow(() -> new NotFoundException("Giảng viên", giangVienId));
        if (body.getMucUuTien() == null || body.getMucUuTien() < 1 || body.getMucUuTien() > 5) {
            body.setMucUuTien(3);
        }
        body.setId(null);
        body.setGiangVien(gv);
        body.setHocKy(hocKy);
        body.setMonHoc(monHoc);
        body.setTrangThai(TrangThaiNguyenVong.CHO_DUYET);
        return repository.save(body);
    }

    @Transactional
    public NguyenVong capNhat(Integer giangVienId, Integer id, NguyenVong body) {
        NguyenVong existing = getOwned(giangVienId, id);
        if (existing.getTrangThai() != TrangThaiNguyenVong.CHO_DUYET) {
            throw new BadRequestException("Chỉ được sửa nguyện vọng đang chờ duyệt");
        }
        if (body.getMucUuTien() != null) {
            existing.setMucUuTien(body.getMucUuTien());
        }
        existing.setCaUuTien(body.getCaUuTien());
        return repository.save(existing);
    }

    @Transactional
    public void xoa(Integer giangVienId, Integer id) {
        NguyenVong existing = getOwned(giangVienId, id);
        if (existing.getTrangThai() != TrangThaiNguyenVong.CHO_DUYET) {
            throw new BadRequestException("Chỉ được xóa nguyện vọng đang chờ duyệt");
        }
        repository.delete(existing);
    }

    @Transactional
    public NguyenVong duyet(Integer id, TrangThaiNguyenVong trangThai) {
        NguyenVong existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nguyện vọng", id));
        if (trangThai != TrangThaiNguyenVong.DA_DUYET && trangThai != TrangThaiNguyenVong.TU_CHOI) {
            throw new BadRequestException("Trạng thái duyệt không hợp lệ");
        }
        existing.setTrangThai(trangThai);
        return repository.save(existing);
    }

    private NguyenVong getOwned(Integer giangVienId, Integer id) {
        NguyenVong existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Nguyện vọng", id));
        if (!existing.getGiangVien().getId().equals(giangVienId)) {
            throw new ForbiddenException("Không phải nguyện vọng của bạn");
        }
        return existing;
    }
}

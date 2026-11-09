package vn.edu.sgu.phancong.report;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import vn.edu.sgu.phancong.entity.*;
import vn.edu.sgu.phancong.entity.enums.LoaiGiangVien;
import vn.edu.sgu.phancong.repository.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final NhomHocPhanRepository nhomHocPhanRepository;
    private final HocKyRepository hocKyRepository;
    private final NamHocRepository namHocRepository;
    private final GiangVienRepository giangVienRepository;
    private final CongTacKhacRepository congTacKhacRepository;
    private final HoatDongNgoaiTruongRepository hoatDongRepository;

    public byte[] xuatMau1(Integer hocKyId, Integer boMonId) {
        HocKy hocKy = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new vn.edu.sgu.phancong.exception.NotFoundException("Học kỳ", hocKyId));
        List<NhomHocPhan> nhoms = nhomHocPhanRepository.findByKeHoachHocKyId(hocKyId).stream()
                .filter(n -> boMonId == null || (n.getKeHoach().getMonHoc().getBoMon() != null
                        && n.getKeHoach().getMonHoc().getBoMon().getId().equals(boMonId)))
                .toList();

        return writeWorkbook(wb -> {
            Sheet sheet = wb.createSheet("Mau1_HK" + hocKy.getSoHk());
            String title = "BẢNG PHÂN CÔNG GIẢNG VIÊN HỌC KỲ " + hocKy.getSoHk()
                    + " - NĂM HỌC " + hocKy.getNamHoc().getTen();
            createTitle(sheet, title, 12);
            String[] headers = {"STT", "Môn học", "Mã HP", "Số TC", "Hệ số HP", "Khoa hiển thị", "Sĩ số",
                    "Nhóm", "Loại", "Tiết thực hiện", "Tiết thực tế", "Giảng viên", "Mã CB"};
            createHeader(sheet, 2, headers);
            int rowIdx = 3;
            int stt = 1;
            for (NhomHocPhan n : nhoms) {
                Row row = sheet.createRow(rowIdx++);
                MonHoc mh = n.getKeHoach().getMonHoc();
                setCell(row, 0, stt++);
                setCell(row, 1, mh.getTen());
                setCell(row, 2, mh.getMaHp());
                setCell(row, 3, mh.getSoTc());
                setCell(row, 4, mh.getHeSoHp());
                setCell(row, 5, n.getKeHoach().getKhoaHienThi());
                setCell(row, 6, n.getKeHoach().getTongSv());
                setCell(row, 7, n.getSoNhom());
                setCell(row, 8, n.getLoai().name());
                setCell(row, 9, n.getSoTietThucHien());
                setCell(row, 10, n.tinhTietThucTe());
                setCell(row, 11, n.getGiangVien() != null ? n.getGiangVien().getHoTen() : "");
                setCell(row, 12, n.getGiangVien() != null ? n.getGiangVien().getMaCb() : "");
            }
            autoSize(sheet, headers.length);
        });
    }

    public byte[] xuatMau2(Integer namHocId) {
        NamHoc namHoc = namHocRepository.findById(namHocId)
                .orElseThrow(() -> new vn.edu.sgu.phancong.exception.NotFoundException("Năm học", namHocId));

        return writeWorkbook(wb -> {
            Sheet sheet = wb.createSheet("Mau2_CongTac");
            createTitle(sheet, "BẢNG TỔNG HỢP TẢI LƯỢNG GIẢNG VIÊN CƠ HỮU - NĂM HỌC " + namHoc.getTen(), 14);
            String[] headers = {"STT", "Mã CB", "Họ tên", "Học vị", "Bộ môn",
                    "HK1", "HK2", "HK3", "Tổng giảng dạy", "Công tác khác", "Tổng CLC", "Tổng cộng",
                    "Định mức", "Trạng thái"};
            createHeader(sheet, 2, headers);
            int rowIdx = 3;
            int stt = 1;
            for (GiangVien gv : giangVienRepository.findByLoai(LoaiGiangVien.CO_HUU)) {
                BigDecimal hk1 = nz(nhomHocPhanRepository.sumTietByGiangVienAndHocKy(gv.getId(), hocKyId(namHocId, 1)));
                BigDecimal hk2 = nz(nhomHocPhanRepository.sumTietByGiangVienAndHocKy(gv.getId(), hocKyId(namHocId, 2)));
                BigDecimal hk3 = nz(nhomHocPhanRepository.sumTietByGiangVienAndHocKy(gv.getId(), hocKyId(namHocId, 3)));
                BigDecimal giangDay = hk1.add(hk2).add(hk3);
                BigDecimal congTac = BigDecimal.ZERO;
                BigDecimal clc = BigDecimal.ZERO;
                for (CongTacKhac c : congTacKhacRepository.findByGiangVienIdAndNamHocId(gv.getId(), namHocId)) {
                    congTac = congTac.add(c.getSoTiet());
                    if (Boolean.TRUE.equals(c.getLaClc())) {
                        clc = clc.add(c.getSoTiet());
                    }
                }
                BigDecimal tong = giangDay.add(congTac);
                String trangThai = tong.compareTo(gv.getDinhMucMax()) > 0 ? "Vượt trần"
                        : tong.compareTo(gv.getDinhMucMin()) < 0 ? "Dưới định mức" : "Đạt";
                Row row = sheet.createRow(rowIdx++);
                setCell(row, 0, stt++);
                setCell(row, 1, gv.getMaCb());
                setCell(row, 2, gv.getHoTen());
                setCell(row, 3, gv.getHocVi());
                setCell(row, 4, gv.getBoMon() != null ? gv.getBoMon().getTen() : "");
                setCell(row, 5, hk1);
                setCell(row, 6, hk2);
                setCell(row, 7, hk3);
                setCell(row, 8, giangDay);
                setCell(row, 9, congTac);
                setCell(row, 10, clc);
                setCell(row, 11, tong);
                setCell(row, 12, gv.getDinhMucMin() + " - " + gv.getDinhMucMax());
                setCell(row, 13, trangThai);
            }
            autoSize(sheet, headers.length);
        });
    }

    public byte[] xuatMau3(Integer namHocId) {
        return xuatGiangVienTheoLoai(namHocId, LoaiGiangVien.MOI_GIANG, "Mau3_MoiGiang",
                "DANH SÁCH GIẢNG VIÊN MỜI GIẢNG NỘI BỘ");
    }

    public byte[] xuatMau4(Integer namHocId) {
        return xuatGiangVienTheoLoai(namHocId, LoaiGiangVien.THINH_GIANG, "Mau4_ThinhGiang",
                "DANH SÁCH GIẢNG VIÊN THỈNH GIẢNG NGOÀI TRƯỜNG");
    }

    private byte[] xuatGiangVienTheoLoai(Integer namHocId, LoaiGiangVien loai, String sheetName, String title) {
        NamHoc namHoc = namHocRepository.findById(namHocId)
                .orElseThrow(() -> new vn.edu.sgu.phancong.exception.NotFoundException("Năm học", namHocId));
        return writeWorkbook(wb -> {
            Sheet sheet = wb.createSheet(sheetName);
            createTitle(sheet, title + " - NĂM HỌC " + namHoc.getTen(), 9);
            String[] headers = {"STT", "Mã CB", "Họ tên", "Học vị", "Đơn vị công tác",
                    "Chuyên ngành", "Môn học", "Nhóm", "Số tiết thực tế"};
            createHeader(sheet, 2, headers);
            int rowIdx = 3;
            int stt = 1;
            for (GiangVien gv : giangVienRepository.findByLoai(loai)) {
                List<NhomHocPhan> nhoms = nhomHocPhanRepository.findByGiangVienId(gv.getId()).stream()
                        .filter(n -> n.getKeHoach().getHocKy().getNamHoc().getId().equals(namHocId))
                        .toList();
                if (nhoms.isEmpty()) {
                    Row row = sheet.createRow(rowIdx++);
                    setCell(row, 0, stt++);
                    setCell(row, 1, gv.getMaCb());
                    setCell(row, 2, gv.getHoTen());
                    setCell(row, 3, gv.getHocVi());
                    setCell(row, 4, gv.getDonViCongTac());
                    setCell(row, 5, gv.getChuyenNganh());
                    setCell(row, 6, "");
                    setCell(row, 7, "");
                    setCell(row, 8, 0);
                    continue;
                }
                for (NhomHocPhan n : nhoms) {
                    Row row = sheet.createRow(rowIdx++);
                    setCell(row, 0, stt);
                    setCell(row, 1, gv.getMaCb());
                    setCell(row, 2, gv.getHoTen());
                    setCell(row, 3, gv.getHocVi());
                    setCell(row, 4, gv.getDonViCongTac());
                    setCell(row, 5, gv.getChuyenNganh());
                    setCell(row, 6, n.getKeHoach().getMonHoc().getTen());
                    setCell(row, 7, n.getSoNhom());
                    setCell(row, 8, n.tinhTietThucTe());
                }
                stt++;
            }
            autoSize(sheet, headers.length);
        });
    }

    public byte[] xuatMau5(Integer namHocId) {
        NamHoc namHoc = namHocRepository.findById(namHocId)
                .orElseThrow(() -> new vn.edu.sgu.phancong.exception.NotFoundException("Năm học", namHocId));
        return writeWorkbook(wb -> {
            Sheet sheet = wb.createSheet("Mau5_HoatDong");
            createTitle(sheet, "HOẠT ĐỘNG TẬP TRUNG NGOÀI TRƯỜNG CỦA SINH VIÊN - NĂM HỌC " + namHoc.getTen(), 10);
            String[] headers = {"STT", "Trình độ", "Khoa", "Nội dung", "Địa điểm", "Học kỳ",
                    "Số ngày", "Từ ngày", "Đến ngày", "Số lượng SV"};
            createHeader(sheet, 2, headers);
            int rowIdx = 3;
            int stt = 1;
            for (HoatDongNgoaiTruong hd : hoatDongRepository.findByNamHocId(namHocId)) {
                Row row = sheet.createRow(rowIdx++);
                setCell(row, 0, stt++);
                setCell(row, 1, hd.getTrinhDo());
                setCell(row, 2, hd.getKhoa());
                setCell(row, 3, hd.getNoiDung());
                setCell(row, 4, hd.getDiaDiem());
                setCell(row, 5, hd.getHocKy());
                setCell(row, 6, hd.getSoNgay());
                setCell(row, 7, hd.getTuNgay());
                setCell(row, 8, hd.getDenNgay());
                setCell(row, 9, hd.getSoLuongSv());
            }
            autoSize(sheet, headers.length);
        });
    }

    private Integer hocKyId(Integer namHocId, int soHk) {
        return hocKyRepository.findByNamHocIdAndSoHk(namHocId, soHk).map(HocKy::getId).orElse(-1);
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private interface WorkbookWriter {
        void write(XSSFWorkbook wb) throws IOException;
    }

    private byte[] writeWorkbook(WorkbookWriter writer) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            writer.write(wb);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException("Lỗi xuất Excel: " + ex.getMessage(), ex);
        }
    }

    private void createTitle(Sheet sheet, String title, int lastCol) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(title);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        cell.setCellStyle(style);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, Math.max(lastCol - 1, 0)));
    }

    private void createHeader(Sheet sheet, int rowIdx, String[] headers) {
        Row row = sheet.createRow(rowIdx);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void setCell(Row row, int col, Object value) {
        Cell cell = row.createCell(col);
        if (value == null) {
            cell.setCellValue("");
        } else if (value instanceof Number number) {
            cell.setCellValue(number.doubleValue());
        } else if (value instanceof java.time.LocalDate date) {
            cell.setCellValue(date.toString());
        } else {
            cell.setCellValue(value.toString());
        }
    }

    private void autoSize(Sheet sheet, int cols) {
        for (int i = 0; i < cols; i++) {
            try {
                sheet.autoSizeColumn(i);
            } catch (Exception ignored) {
            }
        }
    }
}

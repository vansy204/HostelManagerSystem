package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.InvoiceStatisticsResponse;
import com.hostelmanagersystem.entity.manager.*;
import com.hostelmanagersystem.enums.InvoiceStatus;
import com.hostelmanagersystem.enums.InvoiceType;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.InvoiceMapper;
import com.hostelmanagersystem.mapper.UtilityMapper;
import com.hostelmanagersystem.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {
    InvoiceRepository invoiceRepository;
    RoomRepository roomRepository;
    TenantRepository tenantRepository;
    InvoiceMapper invoiceMapper;
    EmailService emailService;
    MongoTemplate mongoTemplate;
    UtilityUsageRepository utilityUsageRepository;
    UtilityConfigRepository utilityConfigRepository;
    UtilityMapper utilityMapper;

    /**
     * Tạo invoice (hóa đơn) theo cấu trúc mới Invoice:
     * Bao gồm rent, điện, nước, wifi, rác, gửi xe, dịch vụ
     */
    public InvoiceResponse createRentInvoice(String ownerId, InvoiceCreateRequest request) {
        // Lấy chỉ số tiện ích
        UtilityUsage usage = utilityUsageRepository.findById(request.getUtilityUsageId())
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_USAGE_NOT_FOUND));

        // Lấy cấu hình phí
        UtilityConfig config = utilityConfigRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_CONFIG_NOT_FOUND));

        // Lấy thông tin phòng
        Room room = roomRepository.findById(usage.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        // Tính tiền điện và nước
        int electricityUsed = usage.getNewElectricity() - Optional.ofNullable(usage.getOldElectricity()).orElse(0);
        int waterUsed = usage.getNewWater() - Optional.ofNullable(usage.getOldWater()).orElse(0);

        double electricityAmount = electricityUsed * config.getElectricityPricePerUnit();
        double waterAmount = waterUsed * config.getWaterPricePerUnit();

        // Tính phí dịch vụ
        double wifiFee = Boolean.TRUE.equals(usage.getIncludeWifi()) ? config.getWifiFee() : 0;
        double garbageFee = Boolean.TRUE.equals(usage.getIncludeGarbage()) ? config.getGarbageFee() : 0;
        double parkingFee = Boolean.TRUE.equals(usage.getIncludeParking()) ? config.getParkingFee() : 0;
        double serviceAmount = wifiFee + garbageFee + parkingFee;

        // Tiền thuê phòng
        double rentAmount = Optional.ofNullable(room.getPrice()).orElse(0.0);

        // Tổng tiền
        double totalAmount = rentAmount + electricityAmount + waterAmount + serviceAmount;

        // Tìm tenant đang ở phòng
        String tenantId = tenantRepository.findActiveTenantByRoomId(usage.getRoomId())
                .map(Tenant::getId)
                .orElse(null); // Có thể null nếu chưa có người thuê

        // Tạo invoice mới
        Invoice invoice = new Invoice();
        invoice.setOwnerId(ownerId);
        invoice.setTenantId(tenantId);
        invoice.setRoomId(usage.getRoomId());
        invoice.setMonth(usage.getMonth());

        invoice.setRentAmount(rentAmount);
        invoice.setElectricityAmount(electricityAmount);
        invoice.setWaterAmount(waterAmount);
        invoice.setWifiFee(wifiFee);
        invoice.setGarbageFee(garbageFee);
        invoice.setParkingFee(parkingFee);
        invoice.setServiceAmount(serviceAmount);
        invoice.setTotalAmount(totalAmount);

        invoice.setStatus(InvoiceStatus.UNPAID);
        invoice.setType(InvoiceType.UTILITY);
        invoice.setDueDate(request.getDueDate());
        invoice.setUsageId(usage.getId());

        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setUpdatedAt(LocalDateTime.now());

        // Lưu và trả về kết quả
        Invoice saved = invoiceRepository.save(invoice);
        return utilityMapper.toInvoiceResponse(saved);
    }


    @Override
    public List<InvoiceResponse> getInvoicesByRoomAndStatus(String ownerId, String roomId, InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByOwnerIdAndRoomIdAndStatus(ownerId, roomId, status);
        return invoices.stream()
                .map(utilityMapper::toInvoiceResponse)
                .toList();
    }

    @Override
    public InvoiceResponse getRentInvoiceDetail(String ownerId, String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_INVOICE_NOT_FOUND));

        if (!invoice.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return utilityMapper.toInvoiceResponse(invoice);
    }

    @Override
    public InvoiceResponse updateInvoiceStatus(String ownerId, String invoiceId, InvoiceStatus newStatus) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_INVOICE_NOT_FOUND));

        if (!invoice.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        invoice.setStatus(newStatus);
        invoice.setUpdatedAt(LocalDateTime.now());

        Invoice updated = invoiceRepository.save(invoice);
        return utilityMapper.toInvoiceResponse(updated);
    }

    @Override
    public List<InvoiceResponse> getRentInvoicesByMonth(String ownerId, String month) {
        YearMonth yearMonth = YearMonth.parse(month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<Invoice> invoices = invoiceRepository.findByOwnerIdAndCreatedAtBetween(ownerId, start, end);

        return invoices.stream()
                .map(utilityMapper::toInvoiceResponse)
                .toList();
    }

    @Override
    public void deleteRentInvoice(String ownerId, String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_INVOICE_NOT_FOUND));

        if (!invoice.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        invoiceRepository.delete(invoice);
    }
    @Override
    public List<InvoiceResponse> getAllInvoicesByOwner(String ownerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return invoiceRepository.findByOwnerId(ownerId, pageable)
                .stream()
                .map(invoiceMapper::toInvoiceResponse)
                .collect(Collectors.toList());
    }

    public List<Invoice> filterInvoicesByKeyword(List<Invoice> invoices, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return invoices; // Nếu keyword rỗng, trả về nguyên list
        }
        String lowerKeyword = keyword.toLowerCase();

        return invoices.stream()
                .filter(invoice -> {
                    String searchField = "";

                    // Lấy description (nếu khác null)
                    if (invoice.getDescription() != null) {
                        searchField += invoice.getDescription() + " ";
                    }
                    // Lấy mã hóa đơn id (nếu khác null)
                    if (invoice.getId() != null) {
                        searchField += invoice.getId();
                    }

                    return searchField.toLowerCase().contains(lowerKeyword);
                })
                .toList();
    }

    @Override
    public List<InvoiceResponse> searchInvoices(String ownerId, String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Invoice> pageInvoices = invoiceRepository.findByOwnerId(ownerId, pageable);

        List<Invoice> filtered = filterInvoicesByKeyword(pageInvoices.getContent(), keyword);

        return filtered.stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }


    @Override
    public InvoiceStatisticsResponse getInvoiceStatistics(String ownerId, String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.atTime(23, 59, 59);

        // Lấy tất cả hóa đơn trong khoảng thời gian
        List<Invoice> invoices = invoiceRepository.findByOwnerIdAndCreatedAtBetween(ownerId, startDateTime, endDateTime);

        // Tính toán thủ công
        long totalInvoices = invoices.size();

        double totalRevenue = invoices.stream()
                .mapToDouble(Invoice::getTotalAmount)
                .sum();

        InvoiceStatisticsResponse stats = new InvoiceStatisticsResponse();
        stats.setTotalInvoices(totalInvoices);
        stats.setTotalRevenue(totalRevenue);

        return stats;
    }


    @Override
    public void sendInvoiceByEmail(String ownerId, String invoiceId, String email) {
        Invoice invoice = invoiceRepository.findByIdAndOwnerId(invoiceId, ownerId)
                .orElseThrow(() -> new RuntimeException("Invoice not found or access denied"));
        UtilityUsage usage = utilityUsageRepository.findById(invoice.getUsageId())
                .orElseThrow(() -> new RuntimeException("Usage not found or access denied"));
        UtilityConfig config = utilityConfigRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Config not found or access denied"));
        InvoiceResponse invoiceResponse = invoiceMapper.toInvoiceResponse(invoice);
        byte[] pdfBytes = generatePdf(invoiceResponse,usage,config); // Giả sử có method generatePdf trả về byte[]

        emailService.sendInvoiceEmail(
                email,
                "Invoice #" + invoiceId,
                "Please find attached your invoice.",
                pdfBytes,
                "invoice-" + invoiceId + ".pdf"
        );
    }

    @Override
    public List<InvoiceResponse> getInvoicesByStatus(String ownerId, InvoiceStatus status) {
        List<Invoice> invoices = invoiceRepository.findByOwnerIdAndStatus(ownerId, status);
        return invoices.stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
    }

    @Override
    public InvoiceResponse markInvoiceAsPaid(String ownerId, String invoiceId) {
        Invoice invoice = invoiceRepository.findByIdAndOwnerId(invoiceId, ownerId)
                .orElseThrow(() -> new RuntimeException("Invoice not found or access denied"));
        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(invoice);
    }

    @Override
    public InvoiceResponse cancelInvoice(String ownerId, String invoiceId) {
        Invoice invoice = invoiceRepository.findByIdAndOwnerId(invoiceId, ownerId)
                .orElseThrow(() -> new RuntimeException("Invoice not found or access denied"));
        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
        return invoiceMapper.toInvoiceResponse(invoice);
    }

    // Giả sử phương thức generatePdf:
    private byte[] generatePdf(InvoiceResponse invoiceResponse, UtilityUsage usage, UtilityConfig config) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 36, 36, 54, 54);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.open();

            // Tạo font hỗ trợ UTF-8
            BaseFont baseFont = BaseFont.createFont("fonts/arial-unicode-ms.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 18, Font.BOLD);
            Font headerFont = new Font(baseFont, 14, Font.BOLD);
            Font normalFont = new Font(baseFont, 12, Font.NORMAL);
            Font boldFont = new Font(baseFont, 12, Font.BOLD);

            // Header công ty
            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new float[]{3, 2});

            // Logo và thông tin công ty
            PdfPCell leftCell = new PdfPCell();
            leftCell.setBorder(Rectangle.NO_BORDER);
            leftCell.addElement(new Paragraph("Công ty Vũ An", headerFont));
            leftCell.addElement(new Paragraph("trangwebhay.vn", normalFont));
            leftCell.addElement(new Paragraph("Công nghiệp số Quốc An", normalFont));
            leftCell.addElement(new Paragraph("Số điện thoại khách hàng: +84 912 345 678", normalFont));
            leftCell.addElement(new Paragraph("Địa chỉ khách hàng: 194 Quang Trung,", normalFont));
            leftCell.addElement(new Paragraph("Hà Đông, Hà Nội, Việt Nam", normalFont));

            // Thông tin hóa đơn
            PdfPCell rightCell = new PdfPCell();
            rightCell.setBorder(Rectangle.NO_BORDER);
            rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            rightCell.addElement(new Paragraph("HÓA ĐƠN", titleFont));
            rightCell.addElement(new Paragraph("Hóa đơn #" + invoiceResponse.getId(), normalFont));
            rightCell.addElement(new Paragraph("Ngày " + formatDate(invoiceResponse.getDueDate()), normalFont));

            headerTable.addCell(leftCell);
            headerTable.addCell(rightCell);
            document.add(headerTable);

            // Đường kẻ phân cách
            document.add(new LineSeparator());
            document.add(Chunk.NEWLINE);

            // Bảng chi tiết dịch vụ
            PdfPTable serviceTable = new PdfPTable(4);
            serviceTable.setWidthPercentage(100);
            serviceTable.setWidths(new float[]{4, 1, 2, 2});

            // Header bảng
            addTableHeader(serviceTable, "Mục", boldFont);
            addTableHeader(serviceTable, "Số lượng", boldFont);
            addTableHeader(serviceTable, "Đơn giá", boldFont);
            addTableHeader(serviceTable, "Thành tiền", boldFont);

            // Dữ liệu dịch vụ
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            // Tiền phòng
            addTableCell(serviceTable, "Tiền phòng tháng " + invoiceResponse.getMonth(), normalFont);
            addTableCell(serviceTable, "1", normalFont);
            addTableCell(serviceTable, formatCurrency(invoiceResponse.getRentAmount()), normalFont);
            addTableCell(serviceTable, formatCurrency(invoiceResponse.getRentAmount()), normalFont);

            // Tiền điện
            if (invoiceResponse.getElectricityAmount() > 0) {
                addTableCell(serviceTable, "Tiền điện", normalFont);
                addTableCell(serviceTable, usage.getNewElectricity()-usage.getOldElectricity() + " kWh", normalFont);
                addTableCell(serviceTable, formatCurrency(config.getElectricityPricePerUnit()), normalFont);
                addTableCell(serviceTable, formatCurrency(invoiceResponse.getElectricityAmount()), normalFont);
            }

            // Tiền nước
            if (invoiceResponse.getWaterAmount() > 0) {
                addTableCell(serviceTable, "Tiền nước", normalFont);
                addTableCell(serviceTable, usage.getNewWater()-usage.getOldWater() + " m³", normalFont);
                addTableCell(serviceTable, formatCurrency(config.getWaterPricePerUnit()), normalFont);
                addTableCell(serviceTable, formatCurrency(invoiceResponse.getWaterAmount()), normalFont);
            }

            // Dịch vụ khác
            if (invoiceResponse.getRentAmount() > 0) {
                addTableCell(serviceTable, "Dịch vụ khác", normalFont);
                addTableCell(serviceTable, "1", normalFont);
                addTableCell(serviceTable, formatCurrency(invoiceResponse.getRentAmount()), normalFont);
                addTableCell(serviceTable, formatCurrency(invoiceResponse.getRentAmount()), normalFont);
            }

            document.add(serviceTable);
            document.add(Chunk.NEWLINE);

            // Thông tin thanh toán
            PdfPTable totalTable = new PdfPTable(2);
            totalTable.setWidthPercentage(100);
            totalTable.setWidths(new float[]{3, 2});

            // Thông tin thanh toán bên trái
            PdfPCell paymentInfoCell = new PdfPCell();
            paymentInfoCell.setBorder(Rectangle.NO_BORDER);
            paymentInfoCell.addElement(new Paragraph("Thông tin Thanh toán", headerFont));
            paymentInfoCell.addElement(new Paragraph("Ngân hàng VIB", normalFont));
            paymentInfoCell.addElement(new Paragraph("Tên tài khoản: Công ty Vũ An", normalFont));
            paymentInfoCell.addElement(new Paragraph("Số tài khoản: 123-456-7890", normalFont));
            paymentInfoCell.addElement(new Paragraph("Ngày thanh toán: " + formatDate(invoiceResponse.getPaymentDate()), normalFont));
            paymentInfoCell.addElement(Chunk.NEWLINE);
            paymentInfoCell.addElement(new Paragraph("xinchao@trangwebhay.vn | 123 Đường ABC,", normalFont));
            paymentInfoCell.addElement(new Paragraph("Thành phố DEF | +84 912 345 678", normalFont));

            // Tổng tiền bên phải
            PdfPCell totalCell = new PdfPCell();
            totalCell.setBorder(Rectangle.NO_BORDER);
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.addElement(new Paragraph("Tổng cộng: " + formatCurrency(invoiceResponse.getTotalAmount()), normalFont));
            totalCell.addElement(new Paragraph("Thuế (0%): 0đ", normalFont));
            totalCell.addElement(Chunk.NEWLINE);
            totalCell.addElement(new Paragraph("Tổng tiền: " + formatCurrency(invoiceResponse.getTotalAmount()), titleFont));

            totalTable.addCell(paymentInfoCell);
            totalTable.addCell(totalCell);
            document.add(totalTable);

            // Trạng thái thanh toán
            document.add(Chunk.NEWLINE);
            Paragraph statusParagraph = new Paragraph("Trạng thái: " + getStatusText(invoiceResponse.getStatus()), boldFont);
            statusParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(statusParagraph);

            // Ghi chú
            if (invoiceResponse.getDescription() != null && !invoiceResponse.getDescription().isEmpty()) {
                document.add(Chunk.NEWLINE);
                document.add(new Paragraph("Ghi chú: " + invoiceResponse.getDescription(), normalFont));
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo PDF: " + e.getMessage(), e);
        }
    }

    private void addTableHeader(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setBorderWidth(1);
        cell.setPhrase(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(1);
        cell.setPhrase(new Phrase(text, font));
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String formatCurrency(double amount) {
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + "đ";
    }

    private String formatDate(LocalDateTime date) {
        if (date == null) return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return date.format(formatter);
    }

    private String getStatusText(InvoiceStatus status) {
        switch (status.toString()) {
            case "paid":
                return "Đã thanh toán";
            case "unpaid":
                return "Chưa thanh toán";
            case "overdue":
                return "Quá hạn";
            case "partial":
                return "Thanh toán một phần";
            default:
                return status.toString();
        }
    }
}

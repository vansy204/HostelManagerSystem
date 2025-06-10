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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

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
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Invoice> invoices = invoiceRepository.findByOwnerId(ownerId, pageable);
        return invoices.stream()
                .map(invoiceMapper::toInvoiceResponse)
                .toList();
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
        InvoiceResponse invoiceResponse = invoiceMapper.toInvoiceResponse(invoice);
        byte[] pdfBytes = generatePdf(invoiceResponse); // Giả sử có method generatePdf trả về byte[]

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
    private byte[] generatePdf(InvoiceResponse invoiceResponse) {
        // Logic tạo PDF byte[] từ dữ liệu invoiceResponse
        return new byte[0]; // ví dụ tạm
    }
}

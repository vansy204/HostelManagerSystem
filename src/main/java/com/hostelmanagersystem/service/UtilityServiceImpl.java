package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.UtilityConfigUpdateRequest;
import com.hostelmanagersystem.dto.request.UtilityInvoiceCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.response.UtilityConfigResponse;
import com.hostelmanagersystem.dto.response.UtilityInvoiceResponse;
import com.hostelmanagersystem.dto.response.UtilityUsageResponse;
import com.hostelmanagersystem.entity.manager.UtilityConfig;
import com.hostelmanagersystem.entity.manager.UtilityInvoice;
import com.hostelmanagersystem.entity.manager.UtilityUsage;
import com.hostelmanagersystem.mapper.UtilityMapper;
import com.hostelmanagersystem.repository.UtilityConfigRepository;
import com.hostelmanagersystem.repository.UtilityInvoiceRepository;
import com.hostelmanagersystem.repository.UtilityUsageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UtilityServiceImpl implements UtilityService{
    UtilityUsageRepository utilityUsageRepository;
    UtilityMapper utilityMapper;
    UtilityInvoiceRepository utilityInvoiceRepository;
    UtilityConfigRepository utilityConfigRepository;

    @Override
    public UtilityUsageResponse createUtilityUsage(String ownerId, UtilityUsageCreateRequest request) {
        // Thêm kiểm tra phân quyền: ownerId phải trùng với chủ phòng
        UtilityUsage usage = utilityMapper.toEntity(request);
        usage.setOwnerId(ownerId);

        // TODO: Có thể kiểm tra dữ liệu đầu vào (chỉ số mới phải >= chỉ số cũ)

        UtilityUsage saved = utilityUsageRepository.save(usage);
        return utilityMapper.toResponse(saved);
    }

    @Override
    public List<UtilityUsageResponse> getUtilityUsagesByMonth(String ownerId, String month) {
        List<UtilityUsage> list = utilityUsageRepository.findByOwnerIdAndMonth(ownerId, month);
        return list.stream().map(utilityMapper::toResponse).toList();
    }

    @Override
    public UtilityInvoiceResponse createUtilityInvoice(String ownerId, UtilityInvoiceCreateRequest request) {
        // 1. Lấy UtilityUsage theo usageId
        UtilityUsage usage = utilityUsageRepository.findById(request.getUsageId())
                .orElseThrow(() -> new RuntimeException("UtilityUsage không tồn tại"));

        // 2. Kiểm tra ownerId trùng với usage.ownerId
        if (!usage.getOwnerId().equals(ownerId)) {
            throw new RuntimeException("Không có quyền tạo hóa đơn cho owner này");
        }

        // 3. Lấy cấu hình đơn giá điện, nước, dịch vụ của owner
        UtilityConfig config = utilityConfigRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Chưa cấu hình đơn giá tiện ích"));

        // 4. Tính tiền điện, nước
        int electricityUsed = usage.getNewElectricity() - usage.getOldElectricity();
        int waterUsed = usage.getNewWater() - usage.getOldWater();

        Integer electricityAmount = electricityUsed * config.getElectricityPricePerUnit();
        Integer waterAmount = waterUsed * config.getWaterPricePerUnit();
        Integer serviceFee = config.getServiceFee() != 0 ? config.getServiceFee() : 0;

        Integer totalAmount = electricityAmount + waterAmount + serviceFee;

        // 5. Tạo UtilityInvoice entity
        UtilityInvoice invoice = new UtilityInvoice();
        invoice.setUsageId(usage.getId());
        invoice.setRoomId(usage.getRoomId());
        invoice.setTenantId(null); // Có thể lấy tenantId qua phòng (cần thêm DB hoặc service lookup)
        invoice.setOwnerId(ownerId);
        invoice.setElectricityAmount(electricityAmount);
        invoice.setWaterAmount(waterAmount);
        invoice.setServiceFee(serviceFee);
        invoice.setTotalAmount(totalAmount);
        invoice.setStatus("UNPAID");
        invoice.setDueDate(request.getDueDate());
        invoice.setCreatedAt(LocalDateTime.now());

        UtilityInvoice saved = utilityInvoiceRepository.save(invoice);

        return utilityMapper.toUtilityInvoiceResponse(saved);
    }

    @Override
    public List<UtilityInvoiceResponse> getUtilityInvoicesByMonth(String ownerId, String month) {
        // Chuyển chuỗi "2025-05" thành khoảng thời gian trong tháng 5/2025
        YearMonth yearMonth = YearMonth.parse(month); // month = "2025-05"
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();         // 2025-05-01T00:00
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59); // 2025-05-31T23:59:59

        List<UtilityInvoice> invoices = utilityInvoiceRepository
                .findByOwnerIdAndCreatedAtBetween(ownerId, start, end);

        return invoices.stream()
                .map(utilityMapper::toUtilityInvoiceResponse)
                .toList();
    }

    @Override
    public UtilityConfigResponse getConfigByOwnerId(String ownerId) {
        UtilityConfig config = utilityConfigRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new RuntimeException("Chưa cấu hình đơn giá tiện ích"));
        return utilityMapper.toUtilityConfigResponse(config);
    }

    @Override
    public UtilityConfigResponse updateConfig(String ownerId, UtilityConfigUpdateRequest request) {
        UtilityConfig config = utilityConfigRepository.findByOwnerId(ownerId)
                .orElse(new UtilityConfig());

        config.setOwnerId(ownerId);
        config.setElectricityPricePerUnit(request.getElectricityPricePerUnit());
        config.setWaterPricePerUnit(request.getWaterPricePerUnit());
        config.setServiceFee(request.getServiceFee());
        config.setUpdatedAt(LocalDateTime.now());

        UtilityConfig saved = utilityConfigRepository.save(config);
        return utilityMapper.toUtilityConfigResponse(saved);
    }
}

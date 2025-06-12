package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityConfigUpdateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageUpdateRequest;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.UtilityConfigResponse;
import com.hostelmanagersystem.dto.response.UtilityUsageResponse;
import com.hostelmanagersystem.entity.manager.*;
import com.hostelmanagersystem.enums.InvoiceStatus;
import com.hostelmanagersystem.enums.InvoiceType;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.UtilityMapper;
import com.hostelmanagersystem.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UtilityServiceImpl implements UtilityService {
    UtilityUsageRepository utilityUsageRepository;
    UtilityMapper utilityMapper;
    InvoiceRepository invoiceRepository;       // đổi từ UtilityInvoiceRepository thành InvoiceRepository
    UtilityConfigRepository utilityConfigRepository;
    RoomRepository roomRepository;
    TenantRepository tenantRepository;

    @Override
    public UtilityUsageResponse createUtilityUsage(String ownerId, UtilityUsageCreateRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        if (!room.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.CAN_NOT_WRITE);
        }

        // Tìm UtilityUsage gần nhất trước tháng hiện tại để lấy chỉ số cũ
        Optional<UtilityUsage> previousUsageOpt = utilityUsageRepository
                .findTopByOwnerIdAndRoomIdOrderByMonthDesc(ownerId, request.getRoomId());

        int oldElectricity = request.getOldElectricity();
        int oldWater = request.getOldWater();

        if (previousUsageOpt.isPresent()) {
            UtilityUsage previousUsage = previousUsageOpt.get();
            if (oldElectricity == 0) {
                oldElectricity = previousUsage.getNewElectricity();
            }
            if (oldWater == 0) {
                oldWater = previousUsage.getNewWater();
            }
        }

        if (request.getNewElectricity() < oldElectricity) {
            throw new AppException(ErrorCode.INVALID_ELECTRICITY_READING);
        }

        if (request.getNewWater() < oldWater) {
            throw new AppException(ErrorCode.INVALID_WATER_READING);
        }

        UtilityUsage usage = utilityMapper.toEntity(request);
        usage.setOwnerId(ownerId);
        usage.setRoomId(room.getId());
        usage.setOldElectricity(oldElectricity);
        usage.setOldWater(oldWater);
        usage.setCreatedAt(LocalDateTime.now());
        usage.setUpdatedAt(LocalDateTime.now());

        UtilityUsage saved = utilityUsageRepository.save(usage);
        return utilityMapper.toResponse(saved);
    }

    @Override
    public UtilityUsageResponse getUtilityUsageDetail(String ownerId, String usageId) {
        UtilityUsage usage = utilityUsageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_USAGE_NOT_FOUND));

        if (!usage.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        return utilityMapper.toResponse(usage);
    }

    @Override
    public UtilityUsageResponse updateUtilityUsage(String ownerId, String usageId, UtilityUsageUpdateRequest request) {
        UtilityUsage usage = utilityUsageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_USAGE_NOT_FOUND));

        if (!usage.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        if (request.getNewElectricity() < usage.getNewElectricity()) {
            throw new AppException(ErrorCode.INVALID_INPUT_ELECTRIC);
        }

        if (request.getNewWater() < usage.getNewWater()) {
            throw new AppException(ErrorCode.INVALID_INPUT_WATER);
        }

        usage.setOldElectricity(request.getOldWater());
        usage.setNewElectricity(request.getNewElectricity());
        usage.setOldWater(request.getOldWater());
        usage.setNewWater(request.getNewWater());
        usage.setIncludeWifi(request.getIncludeWifi() != null ? request.getIncludeWifi() : usage.getIncludeWifi());
        usage.setIncludeGarbage(request.getIncludeGarbage() != null ? request.getIncludeGarbage() : usage.getIncludeGarbage());
        usage.setIncludeParking(request.getIncludeParking() != null ? request.getIncludeParking() : usage.getIncludeParking());
        usage.setUpdatedAt(LocalDateTime.now());

        UtilityUsage updated = utilityUsageRepository.save(usage);
        return utilityMapper.toResponse(updated);
    }

    @Override
    public List<UtilityUsageResponse> getUtilityUsagesByMonth(String ownerId, String month) {
        List<UtilityUsage> list = utilityUsageRepository.findByOwnerIdAndMonth(ownerId, month);
        return list.stream().map(utilityMapper::toResponse).toList();
    }



    @Override
    public void deleteUtilityUsage(String ownerId, String usageId) {
        UtilityUsage usage = utilityUsageRepository.findById(usageId)
                .orElseThrow(() -> new AppException(ErrorCode.UTILITY_USAGE_NOT_FOUND));

        if (!usage.getOwnerId().equals(ownerId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        utilityUsageRepository.delete(usage);
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
        config.setWifiFee(request.getWifiFee());
        config.setGarbageFee(request.getGarbageFee());
        config.setParkingFee(request.getParkingFee());
        config.setUpdatedAt(LocalDateTime.now());

        UtilityConfig saved = utilityConfigRepository.save(config);
        return utilityMapper.toUtilityConfigResponse(saved);
    }
}

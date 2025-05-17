package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.TenantCreationRequest;
import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.request.TenantUpdateRequest;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.TenantStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.TenantMapper;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.repository.TenantRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TenantService {
RoomRepository roomRepository;
    TenantRepository tenantRepository;
   UserRepository userRepository;
    TenantMapper tenantMapper;
    public TenantResponse createTenant(TenantRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Tenant tenant = Tenant.builder()
                .userId(user.getId())
                .roomId(room.getId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
                .status(TenantStatus.PENDING)
                .createAt(LocalDate.now())
                .build();

        tenant = tenantRepository.save(tenant);

        return toResponse(tenant);
    }


    public List<TenantResponse> getRequestsByUser(String userId) {
        return tenantRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public String cancelTenant(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new AppException(ErrorCode.REQUEST_BEING_PROCESSED);
        }

        tenant.setStatus(TenantStatus.CANCELLED);
        tenantRepository.save(tenant);
        return "Bạn đã hủy yêu cầu thuê phòng thành công";

    }


    private TenantResponse toResponse(Tenant tenant) {
        return TenantResponse.builder()
                .id(tenant.getId())
                .roomId(tenant.getRoomId())
                .checkInDate(tenant.getCheckInDate())
                .checkOutDate(tenant.getCheckOutDate())
                .status(tenant.getStatus())
                .build();

    }
    public TenantResponse createTenant(String landlordId, TenantCreationRequest request) {
        Tenant tenant = tenantMapper.toTenant(request);
        tenant.setLandlordId(landlordId);

        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    public List<TenantResponse> getTenantsByLandlord(String landlordId) {
        return tenantRepository.findByLandlordId(landlordId)
                .stream()
                .map(tenantMapper::toTenantResponse)
                .collect(Collectors.toList());
    }

    public TenantResponse updateTenant(String tenantId, TenantUpdateRequest request) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        tenantMapper.updateTenant(tenant, request);
        return tenantMapper.toTenantResponse(tenantRepository.save(tenant));
    }

    public String deleteTenant(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        tenantRepository.delete(tenant);
        return "Tenant deleted successfully";
    }
}
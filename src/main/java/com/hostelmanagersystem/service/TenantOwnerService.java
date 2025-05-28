package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.ModifyTenantRequest;
import com.hostelmanagersystem.dto.request.RoomChangeRequest;
import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.response.TenantHistoryResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class TenantOwnerService {
    TenantRepository tenantRepository;
    RoomRepository roomRepository;
    UserRepository userRepository;
    TenantMapper tenantMapper;

    public List<TenantResponse> getActiveTenantsByOwner(String ownerId) {
        return tenantRepository.findByOwnerIdAndStatus(ownerId, TenantStatus.ACTIVE)
                .stream()
                .map(tenantMapper::toResponse)
                .toList();
    }

    public TenantResponse changeTenantRoom(RoomChangeRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.getTenantId(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        Room newRoom = roomRepository.findByIdAndOwnerId(request.getNewRoomId(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        tenant.setRoomId(newRoom.getId());
        tenantRepository.save(tenant);
        return tenantMapper.toResponse(tenant);
    }

    public TenantResponse updateTenant(TenantRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.getUserId(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        tenant.setFullName(request.getFullName());
        tenant.setPhoneNumber(request.getPhoneNumber());
        tenant.setIdCardNumber(request.getIdentityNumber());
        tenant.setEmail(request.getEmail());
        tenant.setAvatarUrl(request.getAvatarUrl());
        tenant.setCheckInDate(request.getCheckInDate());
        tenant.setCheckOutDate(request.getCheckOutDate());

        tenantRepository.save(tenant);
        return tenantMapper.toResponse(tenant);
    }

    public void modifyTenant(ModifyTenantRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.getTenantId(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if ("END".equalsIgnoreCase(request.getAction())) {
            tenant.setStatus(TenantStatus.INACTIVE);
            tenant.setCheckOutDate(LocalDate.now());
            tenantRepository.save(tenant);
        } else if ("DELETE".equalsIgnoreCase(request.getAction())) {
            tenantRepository.deleteById(tenant.getId());
        } else {
            throw new IllegalArgumentException("Invalid action");
        }
    }

    public List<TenantHistoryResponse> getTenantHistory(String tenantId, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(tenantId, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        return tenantRepository.findHistoryByUserId(tenant.getUserId());
    }
}
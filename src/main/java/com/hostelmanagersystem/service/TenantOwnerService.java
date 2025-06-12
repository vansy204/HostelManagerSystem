package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.*;
import com.hostelmanagersystem.dto.response.TenantHistoryResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.RoomStatus;
import com.hostelmanagersystem.enums.TenantStatus;
import com.hostelmanagersystem.exception.AppException;
import com.hostelmanagersystem.exception.ErrorCode;
import com.hostelmanagersystem.mapper.TenantMapper;
import com.hostelmanagersystem.repository.RoomRepository;
import com.hostelmanagersystem.repository.TenantOwnerRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class TenantOwnerService {
    TenantOwnerRepository tenantRepository;
    RoomRepository roomRepository;
    TenantMapper tenantMapper;

    /**
     * Lấy danh sách tenant của chủ trọ.
     */
    public List<TenantResponse> getAllTenantsForOwner(String ownerId) {
        List<Tenant> tenants = tenantRepository.findByOwnerId(ownerId);
        return tenants.stream()
                .map(tenantMapper::toResponse)
                .collect(Collectors.toList());
    }


    /**
     * Lấy danh sách tenant theo status cụ thể của chủ trọ.
     */
    public List<TenantResponse> getTenantsByStatus(String ownerId, TenantStatus status) {
        return tenantRepository.findByOwnerIdAndStatus(ownerId, status)
                .stream()
                .map(tenantMapper::toResponse)
                .toList();
    }

    /**
     * Chuyển phòng cho tenant nếu hợp lệ.
     */
    public TenantResponse changeTenantRoom(RoomChangeRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.get_id(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        Room oldRoom = roomRepository.findByIdAndOwnerId(tenant.getRoomId(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        Room newRoom = roomRepository.findByIdAndOwnerId(request.getNewRoomId(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

        // Gán phòng mới cho tenant
        tenant.setRoomId(newRoom.getId());
        tenantRepository.save(tenant);

        // Cập nhật trạng thái phòng
        oldRoom.setStatus(RoomStatus.AVAILABLE);
        newRoom.setStatus(RoomStatus.OCCUPIED);

        roomRepository.saveAll(List.of(oldRoom, newRoom));

        return tenantMapper.toResponse(tenant);
    }

    public TenantResponse getTenantById(String tenantId, String ownerId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        return tenantMapper.toResponse(tenant);
    }

    public TenantResponse getTenantByRoomId(String roomId) {
        Tenant tenant = tenantRepository.findTenantByRoomId(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        return tenantMapper.toResponse(tenant);
    }

    /**
     * Cập nhật thông tin tenant.
     */
    public TenantResponse updateTenant(TenantRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.get_id(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        tenant.setFullName(request.getFullName());
        tenant.setPhoneNumber(request.getPhoneNumber());
        tenant.setIdCardNumber(request.getIdCardNumber());
        tenant.setEmail(request.getEmail());
        tenant.setAvatarUrl(request.getAvatarUrl());
        tenant.setCheckInDate(request.getCheckInDate());
        tenant.setCheckOutDate(request.getCheckOutDate());

        tenantRepository.save(tenant);
        return tenantMapper.toResponse(tenant);
    }

    /**
     * Kết thúc hoặc xoá tenant.
     */
    public void endOrDeleteTenant(ModifyTenantRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.get_id(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        TenantStatus newStatus = request.getStatus();

        if (newStatus == null) {
            throw new AppException(ErrorCode.STATUS_MUST_BE_PROVIDED);
        }

        switch (newStatus) {
            case INACTIVE, MOVED_OUT, CANCELLED, COMPLETED -> {
                tenant.setStatus(newStatus);
                tenant.setCheckOutDate(LocalDate.now());
                tenantRepository.save(tenant);
            }
            case REJECTED -> {
                tenantRepository.deleteById(tenant.getId());
            }
            default -> {
                throw new AppException(ErrorCode.INVALID_STATUS);
            }
        }
    }

    /**
     * Lịch sử thuê trọ của 1 tenant (theo userId).
     */
    public List<TenantHistoryResponse> getTenantHistory(String _id, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(_id, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        return tenantRepository.findHistoryByUserId(tenant.getUserId());
    }

    /**
     * Cập nhật trạng thái của tenant (duyệt hoặc từ chối) và trạng thái phòng nếu cần.
     */
    public TenantResponse updateTenantStatusAndRoom(String _id, TenantStatus status, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(_id, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
        }

        if (status == TenantStatus.APPROVED) {
            Room room = roomRepository.findByIdAndOwnerId(tenant.getRoomId(), ownerId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

            room.setStatus(RoomStatus.RESERVED);
            roomRepository.save(room);
        }

        tenant.setStatus(status);
        tenantRepository.save(tenant);

        return tenantMapper.toResponse(tenant);
    }


}
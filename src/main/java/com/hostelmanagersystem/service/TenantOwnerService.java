package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.ModifyTenantRequest;
import com.hostelmanagersystem.dto.request.RoomChangeRequest;
import com.hostelmanagersystem.dto.request.TenantRequest;
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
import com.hostelmanagersystem.repository.TenantRepository;
import com.hostelmanagersystem.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class TenantOwnerService {
    TenantRepository tenantRepository;
    RoomRepository roomRepository;
    UserRepository userRepository;
    TenantMapper tenantMapper;

    public List<TenantResponse> getPendingTenantsByOwner(String ownerId) {
        return tenantRepository.findByOwnerIdAndStatus(ownerId, TenantStatus.PENDING)
                .stream()
                .map(tenantMapper::toResponse)
                .toList();
    }

    public TenantResponse changeTenantRoom(RoomChangeRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.get_id(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        Room newRoom = roomRepository.findByIdAndOwnerId(request.getNewRoomId(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        tenant.setRoomId(newRoom.getId());
        tenantRepository.save(tenant);
        return tenantMapper.toResponse(tenant);
    }

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

    public void endOrDeleteTenant(ModifyTenantRequest request, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(request.get_id(), ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        TenantStatus newStatus = request.getStatus();

        if (newStatus == TenantStatus.INACTIVE) {
            // Kết thúc hợp đồng, set trạng thái INACTIVE, cập nhật ngày trả phòng
            tenant.setStatus(TenantStatus.INACTIVE);
            tenant.setCheckOutDate(LocalDate.now());
            tenantRepository.save(tenant);
        } else if (newStatus == null) {
            throw new IllegalArgumentException("Status must be provided");
        } else if (newStatus == TenantStatus.MOVED_OUT || newStatus == TenantStatus.CANCELLED || newStatus == TenantStatus.COMPLETED) {
            // Nếu muốn mở rộng, xử lý các trạng thái kết thúc hợp đồng khác
            tenant.setStatus(newStatus);
            tenant.setCheckOutDate(LocalDate.now());
            tenantRepository.save(tenant);
        } else if (newStatus == TenantStatus.REJECTED) {
            // Từ chối => xoá tenant khỏi DB
            tenantRepository.deleteById(tenant.getId());
        } else {
            throw new IllegalArgumentException("Invalid status for end/delete operation");
        }
    }

    public List<TenantHistoryResponse> getTenantHistory(String _id, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(_id, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));
        return tenantRepository.findHistoryByUserId(tenant.getUserId());
    }

    public TenantResponse updateTenantStatusAndRoom(String _id, TenantStatus status, String ownerId) {
        Tenant tenant = tenantRepository.findByIdAndOwnerId(_id, ownerId)
                .orElseThrow(() -> new AppException(ErrorCode.TENANT_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_STATUS_CHANGE);
        }

        // Nếu duyệt, cập nhật trạng thái phòng
        if (status == TenantStatus.APPROVED) {
            Room room = roomRepository.findByIdAndOwnerId(tenant.getRoomId(), ownerId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

            // Đặt trạng thái phòng là RESERVED hoặc ACTIVE tùy logic
            room.setStatus(RoomStatus.RESERVED); // hoặc RoomStatus.ACTIVE nếu đã thuê luôn
            roomRepository.save(room);
        }

        // Nếu từ chối thì không cần cập nhật phòng
        tenant.setStatus(status);
        tenantRepository.save(tenant);

        return tenantMapper.toResponse(tenant);
    }
}
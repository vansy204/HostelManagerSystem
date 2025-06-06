package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.identity.User;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import java.util.Optional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor


@Slf4j
public class TenantService {
    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final TenantMapper tenantMapper;


    public TenantResponse createTenant(TenantRequest request) {
        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Validate ngày thuê
        LocalDate checkInDate = request.getCheckInDate();
        if (checkInDate == null || checkInDate.isBefore(LocalDate.now())) {
            throw new AppException(ErrorCode.INVALID_CHECKIN_DATE);
        }

        Integer leaseTerm = room.getLeaseTerm();
        if (leaseTerm == null) {
            throw new AppException(ErrorCode.LEASE_TERM_NOT_DEFINED);
        }

        LocalDate checkOutDate = checkInDate.plusMonths(leaseTerm);
        if (!checkOutDate.isAfter(checkInDate)) {
            throw new AppException(ErrorCode.INVALID_CHECKIN_DATE);
        }

        // Kiểm tra trùng yêu cầu thuê
        Optional<Tenant> existingRequest = tenantRepository
                .findByUserIdAndRoomIdAndStatusNot(user.getId(), room.getId(), TenantStatus.CANCELLED);
        if (existingRequest.isPresent()) {
            throw new AppException(ErrorCode.TENANT_REQUEST_ALREADY_EXISTS);
        }

        // Tạo yêu cầu thuê
        Tenant tenant = Tenant.builder()
                .userId(user.getId())
                .ownerId(room.getOwnerId())
                .roomId(room.getId())
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .status(TenantStatus.PENDING)
                .createAt(LocalDate.now())
                .build();

        tenant = tenantRepository.save(tenant);
        return tenantMapper.toResponse(tenant);
    }



    public List<TenantResponse> getRequestsByUser(String userId) {
        return tenantRepository.findByUserId(userId)
                .stream()
                .map(tenantMapper::toResponse)
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


    @PreAuthorize("hasRole('OWNER')")
    public String approveTenant(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.PENDING) {
            throw new AppException(ErrorCode.REQUEST_BEING_PROCESSED);
        }

        tenant.setStatus(TenantStatus.APPROVED);
        tenantRepository.save(tenant);

        Room room = roomRepository.findById(tenant.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        room.setStatus(RoomStatus.RESERVED);
        roomRepository.save(room);

        return "Đã duyệt yêu cầu thuê phòng. Phòng tạm chuyển sang trạng thái RESERVED.";
    }

    @PreAuthorize("hasRole('OWNER')")
    public String depositTenant(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.APPROVED) {
            throw new AppException(ErrorCode.REQUEST_BEING_PROCESSED);
        }

        tenant.setStatus(TenantStatus.DEPOSITED);
        tenantRepository.save(tenant);
        // Phòng vẫn giữ RESERVED
        return "Đã xác nhận đặt cọc. Phòng tiếp tục giữ trạng thái RESERVED.";
    }

    public String confirmContract(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.DEPOSITED) {
            throw new AppException(ErrorCode.REQUEST_BEING_PROCESSED);
        }

        tenant.setStatus(TenantStatus.CONTRACT_CONFIRMED);
        tenantRepository.save(tenant);

        Room room = roomRepository.findById(tenant.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);

        return "Đã xác nhận hợp đồng. Phòng chuyển sang trạng thái OCCUPIED.";
    }

    public String returnRoom(String tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AppException(ErrorCode.REQUEST_NOT_FOUND));

        if (tenant.getStatus() != TenantStatus.CONTRACT_CONFIRMED) {
            throw new AppException(ErrorCode.REQUEST_BEING_PROCESSED);
        }

        tenant.setStatus(TenantStatus.COMPLETED); // kết thúc hợp đồng
        tenantRepository.save(tenant);

        Room room = roomRepository.findById(tenant.getRoomId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));
        room.setStatus(RoomStatus.CLEANING);
        roomRepository.save(room);

        return "Đã đánh dấu trả phòng. Phòng đang dọn dẹp.";
    }

    public String finishCleaning(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED));

        if (room.getStatus() != RoomStatus.CLEANING) {
            throw new AppException(ErrorCode.ROOM_NOT_IN_CLEANING);
        }

        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.save(room);

        return "Phòng đã sẵn sàng cho thuê lại (AVAILABLE).";
    }



}
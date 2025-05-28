package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.TenantRequest;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

        Optional<Tenant> existingRequest = tenantRepository
                .findByUserIdAndRoomIdAndStatusNot(user.getId(), room.getId(), TenantStatus.CANCELLED);

        if (existingRequest.isPresent()) {
            throw new AppException(ErrorCode.TENANT_REQUEST_ALREADY_EXISTS);
        }


        Tenant tenant = Tenant.builder()
                .userId(user.getId())
                .roomId(room.getId())
                .checkInDate(request.getCheckInDate())
                .checkOutDate(request.getCheckOutDate())
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

}
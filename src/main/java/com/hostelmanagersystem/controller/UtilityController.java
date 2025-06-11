package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.UtilityConfigUpdateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageUpdateRequest;
import com.hostelmanagersystem.dto.response.*;
import com.hostelmanagersystem.entity.manager.UtilityUsage;
import com.hostelmanagersystem.mapper.UtilityMapper;
import com.hostelmanagersystem.repository.UtilityUsageRepository;
import com.hostelmanagersystem.service.UtilityService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/utility")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class UtilityController {
    UtilityService utilityService;
    UtilityUsageRepository utilityUsageRepository;
    UtilityMapper utilityMapper;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/usage")
    public ApiResponse<UtilityUsageResponse> createUtilityUsage(
            @RequestBody @Valid UtilityUsageCreateRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        UtilityUsageResponse response = utilityService.createUtilityUsage(ownerId, request);
        return ApiResponse.<UtilityUsageResponse>builder()
                .result(response)
                .message("Tạo chỉ số tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/usage")
    public ApiResponse<List<UtilityUsageResponse>> getUtilityUsages(
            @RequestParam String month,
            Authentication authentication) {

        String ownerId = authentication.getName();
        List<UtilityUsageResponse> list = utilityService.getUtilityUsagesByMonth(ownerId, month);
        return ApiResponse.<List<UtilityUsageResponse>>builder()
                .result(list)
                .message("Lấy danh sách chỉ số tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/usage/{id}")
    public ApiResponse<UtilityUsageResponse> getUtilityUsageDetail(
            @PathVariable String id,
            Authentication authentication) {

        String ownerId = authentication.getName();
        UtilityUsageResponse response = utilityService.getUtilityUsageDetail(ownerId, id);
        return ApiResponse.<UtilityUsageResponse>builder()
                .result(response)
                .message("Lấy chi tiết chỉ số tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/usage/{id}")
    public ApiResponse<UtilityUsageResponse> updateUtilityUsage(
            @PathVariable String id,
            @RequestBody @Valid UtilityUsageUpdateRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        UtilityUsageResponse response = utilityService.updateUtilityUsage(ownerId, id, request);
        return ApiResponse.<UtilityUsageResponse>builder()
                .result(response)
                .message("Cập nhật chỉ số tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/usage/{id}")
    public ApiResponse<Void> deleteUtilityUsage(
            @PathVariable String id,
            Authentication authentication) {

        String ownerId = authentication.getName();
        utilityService.deleteUtilityUsage(ownerId, id);
        return ApiResponse.<Void>builder()
                .message("Xoá chỉ số tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping
    public ApiResponse<UtilityConfigResponse> getConfig(Authentication authentication) {
        String ownerId = authentication.getName();
        UtilityConfigResponse response = utilityService.getConfigByOwnerId(ownerId);
        return ApiResponse.<UtilityConfigResponse>builder()
                .result(response)
                .message("Lấy cấu hình tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ApiResponse<UtilityConfigResponse> updateConfig(
            @RequestBody UtilityConfigUpdateRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        UtilityConfigResponse response = utilityService.updateConfig(ownerId, request);
        return ApiResponse.<UtilityConfigResponse>builder()
                .result(response)
                .message("Cập nhật cấu hình tiện ích thành công")
                .build();
    }

    @GetMapping("/previous-readings/{roomId}")
    public ApiResponse<UtilityUsageResponse> getPreviousReadings(
            @PathVariable String roomId,
            @RequestParam(required = false) String currentMonth,
            Authentication authentication
    ) {
        String ownerId = authentication.getName();

        Optional<UtilityUsage> previousUsage;

        if (currentMonth != null && !currentMonth.isEmpty()) {
            // Lấy chỉ số của tháng trước tháng hiện tại
            YearMonth current = YearMonth.parse(currentMonth);
            YearMonth previous = current.minusMonths(1);
            String previousMonthStr = previous.toString();

            previousUsage = utilityUsageRepository
                    .findByOwnerIdAndRoomIdAndMonth(ownerId, roomId, previousMonthStr);
        } else {
            // Fallback: lấy record mới nhất
            previousUsage = utilityUsageRepository
                    .findTopByOwnerIdAndRoomIdOrderByMonthDesc(ownerId, roomId);
        }

        UtilityUsageResponse response = previousUsage
                .map(utilityMapper::toResponse)
                .orElse(new UtilityUsageResponse());

        return ApiResponse.<UtilityUsageResponse>builder()
                .result(response)
                .message("Lấy chỉ số tiện ích tháng trước thành công")
                .build();
    }
}

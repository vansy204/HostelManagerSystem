package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.UtilityConfigUpdateRequest;
import com.hostelmanagersystem.dto.request.UtilityInvoiceCreateRequest;
import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.UtilityConfigResponse;
import com.hostelmanagersystem.dto.response.UtilityInvoiceResponse;
import com.hostelmanagersystem.dto.response.UtilityUsageResponse;
import com.hostelmanagersystem.service.UtilityService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/utility")
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PACKAGE, makeFinal = true)
public class UtilityController {
    UtilityService utilityService;

    // Chỉ chủ trọ (ROLE_OWNER) được gọi API này
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/usage")
    public ApiResponse<UtilityUsageResponse> createUtilityUsage(
            @RequestBody @Valid UtilityUsageCreateRequest request,
            Authentication authentication) {

        String ownerId = getOwnerIdFromAuth(authentication);
        UtilityUsageResponse response = utilityService.createUtilityUsage(ownerId, request);
        return ApiResponse.<UtilityUsageResponse>builder()
                .result(response)
                .message("Tạo chỉ số tiện ích thành công")
                .build();
    }

    // Lấy danh sách chỉ số theo tháng cho chủ trọ
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/usage")
    public ApiResponse<List<UtilityUsageResponse>> getUtilityUsages(
            @RequestParam String month,
            Authentication authentication) {

        String ownerId = getOwnerIdFromAuth(authentication);
        List<UtilityUsageResponse> list = utilityService.getUtilityUsagesByMonth(ownerId, month);
        return ApiResponse.<List<UtilityUsageResponse>>builder()
                .result(list)
                .message("Lấy danh sách chỉ số tiện ích thành công")
                .build();
    }

    private String getOwnerIdFromAuth(Authentication auth) {
        // TODO: lấy ownerId từ token hoặc UserDetails
        return auth.getName();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/invoice")
    public ApiResponse<UtilityInvoiceResponse> createUtilityInvoice(
            @RequestBody @Valid UtilityInvoiceCreateRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        UtilityInvoiceResponse response = utilityService.createUtilityInvoice(ownerId, request);
        return ApiResponse.<UtilityInvoiceResponse>builder()
                .result(response)
                .message("Tạo hóa đơn tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/invoice")
    public ApiResponse<List<UtilityInvoiceResponse>> getUtilityInvoices(
            @RequestParam String month,
            Authentication authentication) {

        String ownerId = authentication.getName();
        List<UtilityInvoiceResponse> invoices = utilityService.getUtilityInvoicesByMonth(ownerId, month);
        return ApiResponse.<List<UtilityInvoiceResponse>>builder()
                .result(invoices)
                .message("Lấy danh sách hóa đơn tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping
    public ApiResponse<UtilityConfigResponse> getConfig(Authentication authentication) {
        String landlordId = authentication.getName();
        UtilityConfigResponse response = utilityService.getConfigByLandlordId(landlordId);
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
        String landlordId = authentication.getName();
        UtilityConfigResponse response = utilityService.updateConfig(landlordId, request);
        return ApiResponse.<UtilityConfigResponse>builder()
                .result(response)
                .message("Cập nhật cấu hình tiện ích thành công")
                .build();
    }
}

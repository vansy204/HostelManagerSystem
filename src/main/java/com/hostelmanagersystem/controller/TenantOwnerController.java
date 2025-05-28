package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.ModifyTenantRequest;
import com.hostelmanagersystem.dto.request.RoomChangeRequest;
import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.TenantHistoryResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.service.TenantOwnerService;
import com.hostelmanagersystem.service.TenantService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/owner/tenants")
@PreAuthorize("hasRole('OWNER')")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TenantOwnerController {
    TenantOwnerService tenantService;

    @GetMapping
    public ApiResponse<List<TenantResponse>> getActiveTenants(Authentication authentication) {
        String ownerId = authentication.getName();
        List<TenantResponse> result = tenantService.getActiveTenantsByOwner(ownerId);
        return ApiResponse.<List<TenantResponse>>builder()
                .result(result)
                .message("Lấy danh sách người thuê đang hoạt động thành công")
                .build();
    }

    @PostMapping("/change-room")
    public ApiResponse<TenantResponse> changeRoom(@RequestBody RoomChangeRequest request,
                                                  Authentication authentication) {
        String ownerId = authentication.getName();
        TenantResponse result = tenantService.changeTenantRoom(request, ownerId);
        return ApiResponse.<TenantResponse>builder()
                .result(result)
                .message("Chuyển phòng cho người thuê thành công")
                .build();
    }

    @PutMapping
    public ApiResponse<TenantResponse> updateTenant(@RequestBody TenantRequest request,
                                                    Authentication authentication) {
        String ownerId = authentication.getName();
        TenantResponse result = tenantService.updateTenant(request, ownerId);
        return ApiResponse.<TenantResponse>builder()
                .result(result)
                .message("Cập nhật thông tin người thuê thành công")
                .build();
    }

    @PostMapping("/modify")
    public ApiResponse<Void> modifyTenant(@RequestBody ModifyTenantRequest request,
                                          Authentication authentication) {
        String ownerId = authentication.getName();
        tenantService.modifyTenant(request, ownerId);
        return ApiResponse.<Void>builder()
                .message("Thao tác chỉnh sửa người thuê thành công")
                .build();
    }

    @GetMapping("/{tenantId}/history")
    public ApiResponse<List<TenantHistoryResponse>> getTenantHistory(@PathVariable String tenantId,
                                                                     Authentication authentication) {
        String ownerId = authentication.getName();
        List<TenantHistoryResponse> history = tenantService.getTenantHistory(tenantId, ownerId);
        return ApiResponse.<List<TenantHistoryResponse>>builder()
                .result(history)
                .message("Lấy lịch sử thuê thành công")
                .build();
    }
}

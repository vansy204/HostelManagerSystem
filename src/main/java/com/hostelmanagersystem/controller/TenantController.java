package com.hostelmanagersystem.controller;


import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hostelmanagersystem.dto.request.TenantCreationRequest;
import com.hostelmanagersystem.dto.request.TenantUpdateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@RequestBody TenantRequest request) {
        return ResponseEntity.ok(tenantService.createTenant(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TenantResponse>> getRequestsByUser(@PathVariable String userId) {
        return ResponseEntity.ok(tenantService.getRequestsByUser(userId));
    }

    @DeleteMapping("/{tenantId}")
    public ResponseEntity<String> cancelTenant(@PathVariable String tenantId) {
        String message = tenantService.cancelTenant(tenantId);

        if (message.equals("Bạn đã hủy yêu cầu thuê phòng thành công")) {
            return ResponseEntity.ok(message);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
        }
    
}
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ApiResponse<TenantResponse> createTenant(@RequestBody @Valid TenantCreationRequest request,
                                                    @AuthenticationPrincipal UserDetails user) {
        TenantResponse result = tenantService.createTenant(user.getUsername(), request);
        return ApiResponse.<TenantResponse>builder()
                .result(result)
                .message("Tạo người thuê thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping
    public ApiResponse<List<TenantResponse>> getAllTenants(@AuthenticationPrincipal UserDetails user) {
        List<TenantResponse> result = tenantService.getTenantsByLandlord(user.getUsername());
        return ApiResponse.<List<TenantResponse>>builder()
                .result(result)
                .message("Lấy danh sách người thuê thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{id}")
    public ApiResponse<TenantResponse> updateTenant(@PathVariable String id,
                                                    @RequestBody TenantUpdateRequest request) {
        TenantResponse result = tenantService.updateTenant(id, request);
        return ApiResponse.<TenantResponse>builder()
                .result(result)
                .message("Cập nhật người thuê thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteTenant(@PathVariable String id) {
        String result = tenantService.deleteTenant(id);
        return ApiResponse.<String>builder()
                .result(result)
                .message("Xóa người thuê thành công")
                .build();
    }
}



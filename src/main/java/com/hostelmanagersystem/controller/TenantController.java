package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.TenantRequest;

import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.service.TenantService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
>>>>>>> 510a4e658c5ed4d4c86482667bda523aaa0c3091
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {
    private final TenantService tenantService;

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(
            @RequestBody TenantRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject(); // Lấy userId từ token
        request.setUserId(userId);        // Gắn vào request

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

    //  Duyệt yêu cầu thuê
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{tenantId}/approve")
    public ApiResponse<String> approveTenant(@PathVariable String tenantId) {
        return ApiResponse.<String>builder()
                .result(tenantService.approveTenant(tenantId))
                .build();
    }

    //  Xác nhận đặt cọc
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{tenantId}/deposit")
    public ApiResponse<String> depositTenant(@PathVariable String tenantId) {
        return ApiResponse.<String>builder()
                .result(tenantService.depositTenant(tenantId))
                .build();
    }

    //  Ký hợp đồng
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{tenantId}/confirm-contract")
    public ApiResponse<String> confirmContract(@PathVariable String tenantId) {
        return ApiResponse.<String>builder()
                .result(tenantService.confirmContract(tenantId))
                .build();
    }

    //  Trả phòng
    @PostMapping("/{tenantId}/return")
    public ApiResponse<String> returnRoom(@PathVariable String tenantId) {
        return ApiResponse.<String>builder()
                .result(tenantService.returnRoom(tenantId))
                .build();
    }

    //  Dọn dẹp xong
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/room/{roomId}/clean")
    public ApiResponse<String> finishCleaning(@PathVariable String roomId) {
        return ApiResponse.<String>builder()
                .result(tenantService.finishCleaning(roomId))
                .build();
    }

}
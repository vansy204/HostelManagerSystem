package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.service.TenantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

}
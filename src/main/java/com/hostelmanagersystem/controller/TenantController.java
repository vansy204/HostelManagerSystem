package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.EndContractRequest;
import com.hostelmanagersystem.dto.request.RenewContractRequest;
import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.entity.manager.Room;
import com.hostelmanagersystem.service.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/tenants")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TenantController {
    TenantService tenantService;
    ContractService contractService;
    RenewRequestService  renewRequestService;
    EndRequestService  endRequestService;
    private final InvoiceService invoiceService;

    @PostMapping
    public ApiResponse<TenantResponse> createTenant(
            @RequestBody TenantRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        String userId = jwt.getSubject();
        request.setUserId(userId);

        return ApiResponse.<TenantResponse>builder()
                .result(tenantService.createTenant(request))
                .build();
    }

    @GetMapping("/room")
    public ApiResponse<Room> getMyRoom() {
        return ApiResponse.<Room>builder()
                .result(tenantService.getRoomByTenantId())
                .build();
    }

    @PutMapping("/sign-contract/{contractId}")
    public ApiResponse<String> signContract(@PathVariable String contractId) {
        String message = tenantService.signContract(contractId);
        return ApiResponse.<String>builder()
                .message(message)
                .result("Contract signed successfully")
                .build();
    }


    @GetMapping("/contract/{roomId}")
    public ApiResponse<Contract> getContractByRoomId(@PathVariable String roomId) {
        return ApiResponse.<Contract>builder()
                .result(tenantService.getByRoomId(roomId))
                .build();
    }
    @GetMapping("/getRequest")
    public ApiResponse<TenantResponse> getRequestsByUser() {
        return ApiResponse.<TenantResponse>builder()
                .result(tenantService.getRequestsByUser())
                .build();
    }


    @DeleteMapping("/cancel")
    public ApiResponse<String> cancelTenant() {
        String message = tenantService.cancelTenant();

        return ApiResponse.<String>builder()
                .message(message)
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
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/invoice/{tenantId}")
    public ApiResponse<List<InvoiceResponse>> getInvoiceByTenantId(@PathVariable String tenantId) {
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(invoiceService.getInvoiceByTenant(tenantId))
                .build();
    }
    @PreAuthorize("hasRole('TENANT')")
    @PostMapping("/invoice/pay/{invoiceId}")
    public ApiResponse<String> payInvoice(@PathVariable String invoiceId) {
        invoiceService.payInvoice(invoiceId);
        return ApiResponse.<String>builder()
                .message("Thanh toán thành công")
                .build();
    }


    @PostMapping("/renew")
    @PreAuthorize("hasRole('TENANT')")
    public ApiResponse<String> sendRenewRequest(@RequestBody RenewContractRequest renewContractRequest) {
        renewRequestService.createRequest(renewContractRequest);
        return ApiResponse.<String>builder()
                .message("Đã gửi yêu cầu gia hạn. Vui lòng chờ quản lý duyệt.")
                .build();
    }

    @PreAuthorize("hasRole('TENANT')")
    @PostMapping("/request-end")
    public ApiResponse<String> requestEndContract(@RequestBody EndContractRequest request) {
        endRequestService.createEndRequest(
                request.getTenantId(),
                request.getContractId(),
                request.getReason()
        );

        return ApiResponse.<String>builder()
                .message("Yêu cầu kết thúc hợp đồng đã được gửi. Vui lòng chờ duyệt.")
                .build();
    }



}
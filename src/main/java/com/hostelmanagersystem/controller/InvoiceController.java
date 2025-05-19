package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.enums.InvoiceStatus;
import com.hostelmanagersystem.service.InvoiceService;
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
@RequestMapping("/owner/invoices")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('OWNER')")
public class InvoiceController {
    InvoiceService invoiceService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ApiResponse<InvoiceResponse> createInvoice(
            @RequestBody @Valid InvoiceCreateRequest request,
            Authentication authentication) {
        String landlordId = authentication.getName();
        InvoiceResponse response = invoiceService.createInvoice(landlordId, request);
        return ApiResponse.<InvoiceResponse>builder()
                .result(response)
                .message("Tạo hóa đơn thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{invoiceId}")
    public ApiResponse<InvoiceResponse> getInvoiceById(
            @PathVariable String invoiceId,
            Authentication authentication) {
        String landlordId = authentication.getName();
        InvoiceResponse response = invoiceService.getInvoiceById(landlordId, invoiceId);
        return ApiResponse.<InvoiceResponse>builder()
                .result(response)
                .message("Lấy hóa đơn thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/month/{month}")
    public ApiResponse<List<InvoiceResponse>> getInvoicesByMonth(
            @PathVariable String month,
            Authentication authentication) {
        String landlordId = authentication.getName();
        List<InvoiceResponse> list = invoiceService.getInvoicesByLandlordAndMonth(landlordId, month);
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(list)
                .message("Lấy danh sách hóa đơn theo tháng thành công")
                .build();
    }

    @PreAuthorize("hasRole('RENTER')")
    @GetMapping("/my")
    public ApiResponse<List<InvoiceResponse>> getMyInvoices(Authentication authentication) {
        String tenantId = authentication.getName();
        List<InvoiceResponse> list = invoiceService.getInvoicesByTenant(tenantId);
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(list)
                .message("Lấy danh sách hóa đơn của người thuê thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{invoiceId}/payment-status")
    public ApiResponse<InvoiceResponse> updatePaymentStatus(
            @PathVariable String invoiceId,
            @RequestParam InvoiceStatus status,
            @RequestParam String method,
            Authentication authentication) {
        String landlordId = authentication.getName();
        InvoiceResponse response = invoiceService.updatePaymentStatus(landlordId, invoiceId, status, method);
        return ApiResponse.<InvoiceResponse>builder()
                .result(response)
                .message("Cập nhật trạng thái thanh toán thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{invoiceId}/send-email")
    public ApiResponse<Void> sendInvoiceEmail(
            @PathVariable String invoiceId,
            Authentication authentication) {
        String landlordId = authentication.getName();
        invoiceService.sendInvoiceEmailToTenant(landlordId, invoiceId);
        return ApiResponse.<Void>builder()
                .message("Gửi email hóa đơn thành công")
                .build();
    }
}

package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.CreateInvoiceRequest;
import com.hostelmanagersystem.dto.request.UpdatePaymentStatusRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owner/invoices")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('OWNER')")
public class InvoiceController {
    InvoiceService invoiceService;

    @PostMapping
    public ApiResponse<InvoiceResponse> createInvoice(
            @RequestBody @Valid CreateInvoiceRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        String landlordId = user.getUsername(); // Lấy từ token
        InvoiceResponse response = invoiceService.createInvoice(landlordId, request);

        return ApiResponse.<InvoiceResponse>builder()
                .result(response)
                .message("Tạo hóa đơn thành công")
                .build();
    }
    @PutMapping("/payment-status")
    public ApiResponse<InvoiceResponse> updatePaymentStatus(
            @RequestBody UpdatePaymentStatusRequest request
    ) {
        InvoiceResponse result = invoiceService.updatePaymentStatus(request);
        return ApiResponse.<InvoiceResponse>builder()
                .result(result)
                .message("Cập nhật trạng thái thanh toán thành công")
                .build();
    }
    @GetMapping("/revenue")
    public ApiResponse<Double> getMonthlyRevenue(
            @RequestParam String month,
            @AuthenticationPrincipal UserDetails user
    ) {
        Double revenue = invoiceService.getMonthlyRevenue(user.getUsername(), month);
        return ApiResponse.<Double>builder()
                .result(revenue)
                .message("Doanh thu tháng " + month + " được lấy thành công")
                .build();
    }
}

package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.InvoiceCreateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.InvoiceResponse;
import com.hostelmanagersystem.dto.response.InvoiceStatisticsResponse;
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
@RequestMapping("/invoices")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PreAuthorize("hasRole('OWNER')")
public class InvoiceController {
    InvoiceService invoiceService;

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping()
    public ApiResponse<InvoiceResponse> createUtilityInvoice(
            @RequestBody @Valid InvoiceCreateRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        InvoiceResponse response = invoiceService.createRentInvoice(ownerId, request);
        return ApiResponse.<InvoiceResponse>builder()
                .result(response)
                .message("Tạo hóa đơn tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping()
    public ApiResponse<List<InvoiceResponse>> getUtilityInvoices(
            @RequestParam String month,
            Authentication authentication) {

        String ownerId = authentication.getName();
        List<InvoiceResponse> invoices = invoiceService.getRentInvoicesByMonth(ownerId, month);
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(invoices)
                .message("Lấy danh sách hóa đơn tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/{id}")
    public ApiResponse<InvoiceResponse> getInvoiceDetail(
            @PathVariable String id,
            Authentication authentication) {

        String ownerId = authentication.getName();
        InvoiceResponse response = invoiceService.getRentInvoiceDetail(ownerId, id);
        return ApiResponse.<InvoiceResponse>builder()
                .result(response)
                .message("Lấy chi tiết hóa đơn tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{id}/status")
    public ApiResponse<InvoiceResponse> updateInvoiceStatus(
            @PathVariable String id,
            @RequestParam InvoiceStatus status,
            Authentication authentication) {

        String ownerId = authentication.getName();
        InvoiceResponse response = invoiceService.updateInvoiceStatus(ownerId, id, status);
        return ApiResponse.<InvoiceResponse>builder()
                .result(response)
                .message("Cập nhật trạng thái hóa đơn tiện ích thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/filter")
    public ApiResponse<List<InvoiceResponse>> getInvoicesByRoomAndStatus(
            @RequestParam String roomId,
            @RequestParam InvoiceStatus status,
            Authentication authentication) {

        String ownerId = authentication.getName();
        List<InvoiceResponse> list = invoiceService.getInvoicesByRoomAndStatus(ownerId, roomId, status);
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(list)
                .message("Lấy danh sách hóa đơn theo phòng và trạng thái thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteInvoice(
            @PathVariable String id,
            Authentication authentication) {

        String ownerId = authentication.getName();
        invoiceService.deleteRentInvoice(ownerId, id);
        return ApiResponse.<Void>builder()
                .message("Xoá hóa đơn tiện ích thành công")
                .build();
    }
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner")
    public ApiResponse<List<InvoiceResponse>> getAllInvoicesForOwner(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        String ownerId = authentication.getName();
        List<InvoiceResponse> invoices = invoiceService.getAllInvoicesByOwner(ownerId, page, size);

        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(invoices)
                .message("Lấy danh sách hóa đơn thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/search")
    public ApiResponse<List<InvoiceResponse>> searchInvoices(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size,
            Authentication authentication) {
        String ownerId = authentication.getName();
        List<InvoiceResponse> invoices = invoiceService.searchInvoices(ownerId, keyword, page, size);
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(invoices)
                .message("Tìm kiếm hóa đơn thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/statistics")
    public ApiResponse<InvoiceStatisticsResponse> getStatistics(
            @RequestParam String startDate,
            @RequestParam String endDate,
            Authentication authentication) {
        String ownerId = authentication.getName();
        InvoiceStatisticsResponse stats = invoiceService.getInvoiceStatistics(ownerId, startDate, endDate);
        return ApiResponse.<InvoiceStatisticsResponse>builder()
                .result(stats)
                .message("Lấy thống kê hóa đơn thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{invoiceId}/send-email")
    public ApiResponse<Void> sendInvoiceEmail(
            @PathVariable String invoiceId,
            @RequestParam String email,
            Authentication authentication) {
        String ownerId = authentication.getName();
        invoiceService.sendInvoiceByEmail(ownerId, invoiceId, email);
        return ApiResponse.<Void>builder()
                .message("Gửi email hóa đơn thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/unpaid")
    public ApiResponse<List<InvoiceResponse>> getUnpaidInvoices(
            @RequestParam InvoiceStatus status,
            Authentication authentication) {
        String ownerId = authentication.getName();
        List<InvoiceResponse> unpaidInvoices = invoiceService.getInvoicesByStatus(ownerId, status);
        return ApiResponse.<List<InvoiceResponse>>builder()
                .result(unpaidInvoices)
                .message("Lấy danh sách hóa đơn chưa thanh toán thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{invoiceId}/mark-paid")
    public ApiResponse<InvoiceResponse> markAsPaid(
            @PathVariable String invoiceId,
            Authentication authentication) {
        String ownerId = authentication.getName();
        InvoiceResponse invoice = invoiceService.markInvoiceAsPaid(ownerId, invoiceId);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoice)
                .message("Đánh dấu hóa đơn đã thanh toán thành công")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{invoiceId}/cancel")
    public ApiResponse<InvoiceResponse> cancelInvoice(
            @PathVariable String invoiceId,
            Authentication authentication) {
        String ownerId = authentication.getName();
        InvoiceResponse invoice = invoiceService.cancelInvoice(ownerId, invoiceId);
        return ApiResponse.<InvoiceResponse>builder()
                .result(invoice)
                .message("Hủy hóa đơn thành công")
                .build();
    }
}

package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.*;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.service.ContractService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ContractController {
    ContractService contractService;
    MongoTemplate mongoTemplate;

    @GetMapping
    public ApiResponse<List<ContractResponse>> getAllContracts (Authentication authentication) {
        String ownerId = authentication.getName();
        List<ContractResponse> contracts = contractService.getAllContractsByOwner(ownerId);
        return ApiResponse.<List<ContractResponse>>builder()
                .result(contracts)
                .message("Lấy danh sách hợp đồng thành công")
                .build();
    }

    // 1. Tạo hợp đồng mới
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ApiResponse<ContractResponse> createContract(
            @RequestBody ContractCreateRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        ContractResponse response = contractService.createContract(request, ownerId);
        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Tạo hợp đồng thành công")
                .build();
    }

    // 2. Cập nhật hợp đồng
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{id}")
    public ApiResponse<ContractResponse> updateContract(
            @PathVariable String id,
            @RequestBody ContractUpdateRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        ContractResponse response = contractService.updateContract(id, request, ownerId);
        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Cập nhật hợp đồng thành công")
                .build();
    }

    // 3. Gia hạn hợp đồng
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{id}/renew")
    public ApiResponse<ContractResponse> renewContract(
            @PathVariable String id,
            @RequestBody ContractRenewRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        ContractResponse response = contractService.renewContract(id, request, ownerId);
        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Gia hạn hợp đồng thành công")
                .build();
    }

    // 4. Chấm dứt hợp đồng
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{id}/terminate")
    public ApiResponse<ContractResponse> terminateContract(
            @PathVariable String id,
            @RequestBody ContractTerminationRequest request,
            Authentication authentication) {

        String ownerId = authentication.getName();
        ContractResponse response = contractService.terminateContract(id, request, ownerId);
        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Chấm dứt hợp đồng thành công")
                .build();
    }

    // 5. Lấy hợp đồng theo ID (dành cho chủ trọ hoặc người thuê)
    @PreAuthorize("hasAnyRole('OWNER', 'TENANT')")
    @GetMapping("/{id}")
    public ApiResponse<ContractResponse> getContractById(
            @PathVariable String id,
            Authentication authentication) {

        String userId = authentication.getName();
        ContractResponse response = contractService.getContractById(id, userId);
        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Lấy thông tin hợp đồng thành công")
                .build();
    }

    //     6. Lọc danh sách hợp đồng theo tiêu chí (lọc nâng cao)
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/search")
    public ApiResponse<List<ContractResponse>> searchContracts(
            @RequestBody ContractFilterRequest filterRequest,
            Authentication authentication) {

        String ownerId = authentication.getName();
        filterRequest.setOwnerId(ownerId); // đảm bảo chỉ lọc hợp đồng thuộc owner đang đăng nhập

        List<ContractResponse> result = contractService.searchContracts(filterRequest);
        return ApiResponse.<List<ContractResponse>>builder()
                .result(result)
                .message("Lọc hợp đồng thành công")
                .build();
    }


    // 7. Lấy hợp đồng theo chủ trọ
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owner")
    public ApiResponse<List<ContractResponse>> getContractsByOwner(Authentication authentication) {
        String ownerId = authentication.getName();
        List<ContractResponse> result = contractService.getContractsByOwner(ownerId);
        return ApiResponse.<List<ContractResponse>>builder()
                .result(result)
                .message("Lấy danh sách hợp đồng của chủ trọ thành công")
                .build();
    }

    // 8. Lấy hợp đồng của người thuê
    @PreAuthorize("hasRole('TENANT')")
    @GetMapping("/tenant")
    public ApiResponse<List<ContractResponse>> getContractsByTenant(Authentication authentication) {
        String tenantId = authentication.getName();
        List<ContractResponse> result = contractService.getContractsByTenant(tenantId);
        return ApiResponse.<List<ContractResponse>>builder()
                .result(result)
                .message("Lấy danh sách hợp đồng của người thuê thành công")
                .build();
    }

    // 9. Lấy lịch sử hợp đồng của một người thuê (có thể dùng cho thống kê hoặc hiển thị chi tiết)
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/tenant/{tenantId}/history")
    public ApiResponse<List<ContractResponse>> getContractHistoryByTenant(
            @PathVariable String tenantId,
            Authentication authentication) {

        String ownerId = authentication.getName();
        List<ContractResponse> result = contractService.getContractHistoryByTenant(tenantId, ownerId);
        return ApiResponse.<List<ContractResponse>>builder()
                .result(result)
                .message("Lấy lịch sử hợp đồng của người thuê thành công")
                .build();
    }

    // 10. Tải file PDF hợp đồng
//    @PreAuthorize("hasAnyRole('OWNER', 'TENANT')")
//    @GetMapping("/{id}/pdf")
//    public ResponseEntity<byte[]> downloadContractPdf(
//            @PathVariable String id,
//            Authentication authentication) {
//
//        String userId = authentication.getName();
//        byte[] pdf = contractService.getContractPdf(id, userId);
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contract-" + id + ".pdf")
//                .contentType(MediaType.APPLICATION_PDF)
//                .contentLength(pdf.length)
//                .body(pdf);
//    }

    // 11. Duyệt hợp đồng (OWNER xác nhận)
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{id}/approve")
    public ApiResponse<ContractResponse> approveContract(
            @PathVariable String id,
            Authentication authentication) {

        String ownerId = authentication.getName();
        ContractResponse response = contractService.approveContract(id, ownerId);

        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Duyệt hợp đồng thành công")
                .build();
    }

    // 12. Ký hợp đồng (TENANT ký)
    @PostMapping("/{id}/sign")
    public ApiResponse<ContractResponse> signContract(@PathVariable String id) {
        ContractResponse response = contractService.signContract(id);

        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Ký hợp đồng thành công")
                .build();
    }

    // 13. Hủy hợp đồng (OWNER hủy trước khi hiệu lực)
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{id}/cancel")
    public ApiResponse<ContractResponse> cancelContract(
            @PathVariable String id,
            Authentication authentication) {

        String ownerId = authentication.getName();
        ContractResponse response = contractService.cancelContract(id, ownerId);

        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Hủy hợp đồng thành công")
                .build();
    }

    @PutMapping("/{id}/confirm-deposit")
    public ApiResponse<ContractResponse> confirmDeposit(
            @PathVariable String id,
            Authentication authentication
    ) {
        String ownerId = authentication.getName();
        ContractResponse response = contractService.confirmDepositPayment(id, ownerId);

        return ApiResponse.<ContractResponse>builder()
                .result(response)
                .message("Xác nhận thanh toán tiền cọc thành công")
                .build();
    }
}
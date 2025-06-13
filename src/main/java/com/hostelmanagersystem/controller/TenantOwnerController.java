package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.*;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.TenantHistoryResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.RenewRequest;
import com.hostelmanagersystem.entity.manager.EndRequest;
import com.hostelmanagersystem.enums.RequestStatus;
import com.hostelmanagersystem.enums.TenantStatus;
import com.hostelmanagersystem.service.EndRequestService;
import com.hostelmanagersystem.service.RenewRequestService;
import com.hostelmanagersystem.service.TenantOwnerService;
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
    RenewRequestService renewRequestService;
    EndRequestService endRequestService;


    @GetMapping
    public ApiResponse<List<TenantResponse>> getAllTenants(
            Authentication authentication) {

        String ownerId = authentication.getName();
        List<TenantResponse> tenants = tenantService.getAllTenantsForOwner(ownerId);

        return ApiResponse.<List<TenantResponse>>builder()
                .result(tenants)
                .message("Lấy danh sách toàn bộ người thuê thành công")
                .build();
    }

    @GetMapping("/{tenantId}")
    public ApiResponse<TenantResponse> getTenantById(
            Authentication authentication,
            @PathVariable String tenantId) {

        String ownerId = authentication.getName();
        TenantResponse response = tenantService.getTenantById(tenantId, ownerId);

        return ApiResponse.<TenantResponse>builder()
                .result(response)
                .message("Lấy thông tin người thuê thành công")
                .build();
    }


    @GetMapping("/{ownerId}/tenants")
    public ApiResponse<List<TenantResponse>> getTenantsByStatus(
            @PathVariable String ownerId,
            @RequestParam TenantStatus status) {

        List<TenantResponse> tenants = tenantService.getTenantsByStatus(ownerId, status);
        return ApiResponse.<List<TenantResponse>>builder()
                .result(tenants)
                .message("Lấy danh sách người thuê theo trạng thái thành công")
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
        tenantService.endOrDeleteTenant(request, ownerId);
        return ApiResponse.<Void>builder()
                .message("Thao tác chỉnh sửa người thuê thành công")
                .build();
    }

    @GetMapping("/{_id}/history")
    public ApiResponse<List<TenantHistoryResponse>> getTenantHistory(@PathVariable String _id,
                                                                     Authentication authentication) {
        String ownerId = authentication.getName();
        List<TenantHistoryResponse> history = tenantService.getTenantHistory(_id, ownerId);
        return ApiResponse.<List<TenantHistoryResponse>>builder()
                .result(history)
                .message("Lấy lịch sử thuê thành công")
                .build();
    }

    @PutMapping("/{_id}/status")
    public ApiResponse<TenantResponse> approveOrRejectTenant(
            @PathVariable String _id,
            @RequestParam TenantStatus status,
            Authentication authentication) {

        String ownerId = authentication.getName();
        TenantResponse result = tenantService.updateTenantStatusAndRoom(_id, status, ownerId);

        String message = switch (status) {
            case APPROVED -> "Phê duyệt người thuê thành công";
            case REJECTED -> "Từ chối người thuê thành công";
            default -> "Cập nhật trạng thái người thuê thành công";
        };

        return ApiResponse.<TenantResponse>builder()
                .result(result)
                .message(message)
                .build();
    }
    @GetMapping("/room/{roomId}")
    public ApiResponse<TenantResponse> getTenantByRoomId(
            @PathVariable("roomId") String roomId) {
        TenantResponse response = tenantService.getTenantByRoomId(roomId);
        return ApiResponse.<TenantResponse>builder()
                .result(response)
                .message("Lấy thông tin người thuê theo phòng thành công")
                .build();
    }
    //Đồng ý gia hạn
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/renew/{requestId}/approve")
    public ApiResponse<String> approveRenewRequest(@PathVariable String requestId) {
        renewRequestService
                .approveRequest(requestId);
        return ApiResponse.<String>builder()
                .message("Đã duyệt gia hạn hợp đồng thành công.")
                .build();
    }
    //Từ chối gia hạn
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/renew/{requestId}/reject")
    public ApiResponse<String> rejectRenewRequest(@PathVariable String requestId) {
        renewRequestService.rejectRequest(requestId);
        return ApiResponse.<String>builder()
                .message("Đã từ chối yêu cầu gia hạn.")
                .build();
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/renew/requests")
    public ApiResponse<List<RenewRequest>> getAllRenewRequests(
            @RequestParam(required = false) RequestStatus status) {

        List<RenewRequest> list = (status != null)
                ? renewRequestService.getRequestsByStatus(status)
                : renewRequestService.getAllRequests();

        return ApiResponse.<List<RenewRequest>>builder()
                .result(list)
                .build();
    }


    //lấy danh sách yêu cầu hủy hợp đồng sowsm
    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/end-requests")
    public ApiResponse<List<EndRequest>> getPendingEndRequests() {
        return ApiResponse.<List<EndRequest>>builder()
                .result(endRequestService.getAllPendingRequests())
                .message("Danh sách yêu cầu kết thúc hợp đồng đang chờ duyệt")
                .build();
    }

    //xác nhận yêu cầu kết thúc hợp đồng sớm
    @PreAuthorize("hasRole('OWNER')") // hoặc ADMIN nếu bạn muốn
    @PostMapping("/confirm-end-request")
    public ApiResponse<String> confirmEndRequest(@RequestParam String requestId) {
        endRequestService.confirmRequest(requestId); // xử lý bên service
        return ApiResponse.<String>builder()
                .message("Hợp đồng đã được kết thúc thành công.")
                .build();
    }

}

package com.hostelmanagersystem.controller;

import com.hostelmanagersystem.dto.request.NotificationCreateRequest;
import com.hostelmanagersystem.dto.response.ApiResponse;
import com.hostelmanagersystem.dto.response.NotificationResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationController {
    NotificationService notificationService;

    @PostMapping("/send")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<Void> sendNotification(
            @RequestBody NotificationCreateRequest request,
            @AuthenticationPrincipal User userDetails) {
        notificationService.sendNotification(request, userDetails.getId());
        return ApiResponse.<Void>builder()
                .message("Gửi thông báo thành công")
                .build();
    }

    @GetMapping("/received")
    @PreAuthorize("hasRole('TENANT')")
    public ApiResponse<List<NotificationResponse>> getReceivedNotifications(
            @AuthenticationPrincipal User userDetails) {
        List<NotificationResponse> result = notificationService.getNotificationsForRecipient(userDetails.getId());
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(result)
                .message("Lấy danh sách thông báo nhận thành công")
                .build();
    }

    @GetMapping("/sent")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<List<NotificationResponse>> getSentNotifications(
            @AuthenticationPrincipal User userDetails) {
        List<NotificationResponse> result = notificationService.getNotificationsFromSender(userDetails.getId());
        return ApiResponse.<List<NotificationResponse>>builder()
                .result(result)
                .message("Lấy danh sách thông báo đã gửi thành công")
                .build();
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasRole('TENANT')")
    public ApiResponse<Void> markAsRead(
            @PathVariable String id,
            @AuthenticationPrincipal User userDetails) {
        notificationService.markAsRead(id, userDetails.getId());
        return ApiResponse.<Void>builder()
                .message("Đánh dấu đã đọc thành công")
                .build();
    }
}

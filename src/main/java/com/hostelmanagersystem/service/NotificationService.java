package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.NotificationCreateRequest;
import com.hostelmanagersystem.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    void sendNotification(NotificationCreateRequest request, String senderId);
    List<NotificationResponse> getNotificationsForRecipient(String recipientId);
    List<NotificationResponse> getNotificationsFromSender(String senderId);
    void markAsRead(String notificationId, String userId);
    void sendMonthlyRentReminders();
    void sendContractExpiryReminders();
}

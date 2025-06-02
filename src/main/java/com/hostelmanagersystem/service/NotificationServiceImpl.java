package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.NotificationCreateRequest;
import com.hostelmanagersystem.dto.response.NotificationResponse;
import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.entity.manager.Notification;
import com.hostelmanagersystem.enums.ContractStatus;
import com.hostelmanagersystem.mapper.NotificationMapper;
import com.hostelmanagersystem.repository.ContractRepository;
import com.hostelmanagersystem.repository.NotificationRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    NotificationRepository notificationRepository;
    NotificationMapper notificationMapper;
    ContractRepository contractRepository;

    @Override
    public void sendNotification(NotificationCreateRequest request, String senderId) {
        for (String recipientId : request.getRecipientIds()) {
            Notification notification = notificationMapper.toEntity(request, senderId, recipientId);
            notificationRepository.save(notification);
        }
    }

    @Override
    public List<NotificationResponse> getNotificationsForRecipient(String recipientId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(recipientId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getNotificationsFromSender(String senderId) {
        return notificationRepository.findBySenderIdOrderByCreatedAtDesc(senderId).stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipientId().equals(userId)) {
            throw new AccessDeniedException("Not your notification");
        }

        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    @Override
    @Scheduled(cron = "0 0 8 1 * ?") // Mỗi tháng ngày 1 lúc 08:00 sáng
    public void sendMonthlyRentReminders() {
        List<Contract> activeContracts = contractRepository.findByStatus(ContractStatus.ACTIVE); // phải triển khai

        for (Contract contract : activeContracts) {
            String tenantId = contract.getTenantId();
            String roomName = contract.getRoomId(); // nếu có
            String month = YearMonth.now().format(DateTimeFormatter.ofPattern("MM/yyyy"));

            Notification notification = new Notification();
            notification.setRecipientId(tenantId);
            notification.setSenderId(contract.getOwnerId());
            notification.setTitle("Nhắc thanh toán tiền trọ");
            notification.setMessage("Vui lòng thanh toán tiền trọ cho phòng " + roomName + " trong tháng " + month + ".");
            notification.setType("RENT_REMINDER");
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
        }
    }

    @Override
    @Scheduled(cron = "0 0 9 * * ?") // Mỗi ngày lúc 09:00 sáng
    public void sendContractExpiryReminders() {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(7); // báo trước 7 ngày

        List<Contract> expiringContracts = contractRepository.findByEndDate(targetDate);

        for (Contract contract : expiringContracts) {
            Notification notification = new Notification();
            notification.setRecipientId(contract.getTenantId());
            notification.setSenderId(contract.getOwnerId());
            notification.setTitle("Hợp đồng sắp hết hạn");
            notification.setMessage("Hợp đồng thuê phòng " + contract.getRoomId() + " sẽ hết hạn vào ngày " +
                    contract.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ".");
            notification.setType("CONTRACT_EXPIRY");
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());

            notificationRepository.save(notification);
        }
    }
}

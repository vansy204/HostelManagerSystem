package com.hostelmanagersystem.entity.manager;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(value = "notifications")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {
    @MongoId
    String id;

    String recipientId; // ID của người nhận (tenant)
    String senderId;    // ID của người gửi (owner)

    String title;
    String message;
    String type; // Ví dụ: "WARNING", "MAINTENANCE", "EVENT"

    Boolean isRead = false;
    LocalDateTime createdAt = LocalDateTime.now();
}

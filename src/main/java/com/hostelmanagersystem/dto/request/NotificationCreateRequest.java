package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationCreateRequest {
    List<String> recipientIds; // Gửi nhiều tenant
    String title;
    String message;
    String type;
}

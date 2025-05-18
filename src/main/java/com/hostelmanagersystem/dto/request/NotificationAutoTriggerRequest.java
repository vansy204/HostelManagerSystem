package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationAutoTriggerRequest {
    String type; // "RENT_REMINDER"
    String messageTemplate; // "Nhắc bạn thanh toán tiền phòng tháng {month}."
}

package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RentInvoiceCreateRequest {
    String tenantId;
    String roomId;
    String month; // yyyy-MM
    Double rentAmount;
    LocalDateTime dueDate;
    String description;
}

package com.hostelmanagersystem.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityInvoiceResponse {
    String id;
    String usageId;
    String roomId;
    String tenantId;
    Integer electricityAmount;
    Integer waterAmount;
    Integer serviceFee;
    Integer totalAmount;
    String status;
    LocalDate dueDate;
    LocalDateTime createdAt;
}

package com.hostelmanagersystem.dto.response;

import com.hostelmanagersystem.enums.InvoiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {
    String id;
    String ownerId;
    String tenantId;
    String roomId;
    String month;

    double rentAmount;
    double electricityAmount;
    double waterAmount;
    double serviceAmount;
    double totalAmount;

    InvoiceStatus status;
    LocalDateTime paymentDate;
    String paymentMethod;

    LocalDateTime createdAt;
}

package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateInvoiceRequest {
    String tenantId;
    String roomId;
    String month; // "2025-05"
    double rentAmount;
    double electricityAmount;
    double waterAmount;
    double serviceAmount;
}

package com.hostelmanagersystem.dto.request;

import com.hostelmanagersystem.enums.InvoiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdatePaymentStatusRequest {
    String invoiceId;
    InvoiceStatus status; // PAID, UNPAID, OVERDUE
    String paymentMethod;
    LocalDateTime paymentDate;
}

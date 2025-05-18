package com.hostelmanagersystem.entity.manager;

import com.hostelmanagersystem.enums.InvoiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(value = "invoices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invoice {
    @MongoId
    String id;

    String landlordId;
    String tenantId;
    String roomId;

    String month; // "2025-05"
    double rentAmount;
    double electricityAmount;
    double waterAmount;
    double serviceAmount;
    double totalAmount;

    InvoiceStatus status; // UNPAID, PAID, OVERDUE
    LocalDateTime paymentDate;
    String paymentMethod; // CASH, BANK_TRANSFER, ONLINE

    LocalDateTime createdAt;

//    String electronicCode;
}

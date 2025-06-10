package com.hostelmanagersystem.entity.manager;

import com.hostelmanagersystem.enums.InvoiceStatus;
import com.hostelmanagersystem.enums.InvoiceType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
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

    String ownerId;
    String tenantId;
    String roomId;
    String month; // YYYY-MM

    Double rentAmount;
    Double electricityAmount;
    Double waterAmount;

    // Thay vì gộp lại thành 1 trường serviceAmount mơ hồ, tách chi tiết:
    Double wifiFee;
    Double garbageFee;
    Double parkingFee;
    Double serviceAmount;

    Double totalAmount; // = rent + electricity + water + wifi + garbage + parking

    InvoiceStatus status; // UNPAID, PAID, OVERDUE
    String description;
    InvoiceType type;
    LocalDateTime paymentDate;
    String paymentMethod; // CASH, BANK_TRANSFER, ONLINE

    LocalDateTime dueDate;

    String usageId; // liên kết với chỉ số tiện ích

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

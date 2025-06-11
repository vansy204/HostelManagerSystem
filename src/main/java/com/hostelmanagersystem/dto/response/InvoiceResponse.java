package com.hostelmanagersystem.dto.response;

import com.hostelmanagersystem.enums.InvoiceStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InvoiceResponse {
    String id;
    String tenantId;
    String roomId;
    String month;

    Double rentAmount;
    Double electricityAmount;
    Double waterAmount;
    Double wifiFee;
    Double garbageFee;
    Double parkingFee;
    Double totalAmount;

    InvoiceStatus status;
    String type;
    String description;
    LocalDateTime paymentDate;
    String paymentMethod;

    LocalDateTime dueDate;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

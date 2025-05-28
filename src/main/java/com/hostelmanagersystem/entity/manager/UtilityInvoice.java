package com.hostelmanagersystem.entity.manager;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "utility_usages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityInvoice {
    @MongoId
    String id;

    String usageId;   // liên kết với UtilityUsage
    String roomId;
    String tenantId;
    String ownerId;

    Integer electricityAmount; // số tiền điện tính được
    Integer waterAmount;       // số tiền nước tính được
    Integer serviceFee;        // phí dịch vụ nếu có (theo config của owner)

    Integer totalAmount;       // tổng tiền (điện + nước + dịch vụ)

    String status;             // TRẠNG THÁI: PAID, UNPAID, OVERDUE

    LocalDate dueDate;

    LocalDateTime createdAt = LocalDateTime.now();
}

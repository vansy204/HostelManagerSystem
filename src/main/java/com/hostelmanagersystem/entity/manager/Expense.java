package com.hostelmanagersystem.entity.manager;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import java.time.LocalDate;

@Document(value = "rooms")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Expense {
    @MongoId
    String id;

    String userId;
    Double amount;
    String purpose; // Mục đích: sửa chữa, điện nước,...
    String note;
    LocalDate date;
}

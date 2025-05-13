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
public class Income {
    @MongoId
    String id;
    String userId;
    Double amount;
    String source; // Nguồn thu: tiền thuê, đặt cọc,...
    String note;
    LocalDate date;
}

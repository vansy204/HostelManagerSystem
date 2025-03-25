package com.hostelmanagersystem.entity.manager;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document(value = "contracts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contract {
    @MongoId
    String id;
    String tenantId;
    String roomId;
    LocalDate startDate;
    LocalDate endDate;
    Double deposit;
    Double monthlyPrice;
    String terms;
    LocalDate createAt;
}

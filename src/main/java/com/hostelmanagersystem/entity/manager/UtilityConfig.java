package com.hostelmanagersystem.entity.manager;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "utility_configs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityConfig {
    @MongoId
    String id;

    String ownerId;

    Integer electricityPricePerUnit; // giá điện theo đơn vị

    Integer waterPricePerUnit;       // giá nước theo đơn vị

    Integer wifiFee;
    Integer garbageFee;
    Integer parkingFee;

    LocalDateTime createdAt = LocalDateTime.now();

    LocalDateTime updatedAt;
}

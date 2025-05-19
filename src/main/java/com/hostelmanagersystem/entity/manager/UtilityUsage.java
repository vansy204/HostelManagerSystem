package com.hostelmanagersystem.entity.manager;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document(collection = "utility_usages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityUsage {
    @MongoId
    String id;

    String roomId;
    String landlordId;
    String month; // "2025-05"

    Integer oldElectricity;
    Integer newElectricity;

    Integer oldWater;
    Integer newWater;

    LocalDateTime createdAt = LocalDateTime.now();
}

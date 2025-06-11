package com.hostelmanagersystem.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityUsageResponse {
    String id;
    String ownerId;
    String roomId;

    Integer oldElectricity;
    Integer newElectricity;

    Integer oldWater;
    Integer newWater;

    Boolean includeWifi;
    Boolean includeGarbage;
    Boolean includeParking;

    String month;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}

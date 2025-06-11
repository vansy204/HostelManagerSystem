package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.YearMonth;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityUsageCreateRequest {
    String roomId;

    Integer oldElectricity; // optional
    Integer newElectricity;

    Integer oldWater;       // optional
    Integer newWater;

    Boolean includeWifi;
    Boolean includeGarbage;
    Boolean includeParking;

    String month;
}

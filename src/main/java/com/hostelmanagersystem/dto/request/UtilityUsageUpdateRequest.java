package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityUsageUpdateRequest {
    Integer oldElectricity;
    Integer newElectricity;
    Integer oldWater;
    Integer newWater;
    Boolean includeWifi;
    Boolean includeGarbage;
    Boolean includeParking;
}

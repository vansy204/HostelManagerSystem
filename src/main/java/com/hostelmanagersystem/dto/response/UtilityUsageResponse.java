package com.hostelmanagersystem.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityUsageResponse {
    String id;
    String roomId;
    String month;
    Integer oldElectricity;
    Integer newElectricity;
    Integer oldWater;
    Integer newWater;
    LocalDateTime createdAt;
}

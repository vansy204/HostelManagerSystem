package com.hostelmanagersystem.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UtilityConfigResponse {
    String landlordId;
    Integer electricityPricePerUnit;
    Integer waterPricePerUnit;
    Integer serviceFee;
}

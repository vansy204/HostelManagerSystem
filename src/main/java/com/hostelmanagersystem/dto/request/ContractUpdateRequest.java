package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractUpdateRequest {
    LocalDate endDate;
    Double monthlyPrice;
    String terms;
}

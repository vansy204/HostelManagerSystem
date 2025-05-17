package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractCreateRequest {
    String tenantId;
    String roomId;
    LocalDate startDate;
    LocalDate endDate;
    Double deposit;
    Double monthlyPrice;
    String terms;
}

package com.hostelmanagersystem.dto.request;

import com.hostelmanagersystem.enums.ContractStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractFilterRequest {
    String ownerId;
    String tenantName;
    String roomId;
    ContractStatus status;
    LocalDate startDateFrom;
    LocalDate startDateTo;
    LocalDate endDateFrom;
    LocalDate endDateTo;
}

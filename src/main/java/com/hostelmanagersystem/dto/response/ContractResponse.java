package com.hostelmanagersystem.dto.response;

import com.hostelmanagersystem.enums.ContractStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractResponse {
    String id;
    String tenantId;
    String roomId;
    LocalDate startDate;
    LocalDate endDate;
    Double deposit;
    Double monthlyPrice;
    String terms;
    ContractStatus status;
    String pdfUrl;
    LocalDate createdAt;
    LocalDate updatedAt;
}

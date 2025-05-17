package com.hostelmanagersystem.dto.response;

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
    String roomId;
    String tenantId;
    LocalDate startDate;
    LocalDate endDate;
    Double deposit;
    Double monthlyPrice;
    String status;
    String pdfUrl;
}

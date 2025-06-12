package com.hostelmanagersystem.dto.request;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;


@Document("renew_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RenewContractRequest {
    private String tenantId;
    private String contractId;
    private int additionalMonths;
    private String reason;
}


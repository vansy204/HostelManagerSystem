package com.hostelmanagersystem.entity;

import com.hostelmanagersystem.enums.RequestStatus;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document("renew_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RenewRequest {
    @MongoId
    private String id;

    private String tenantId;
    private String contractId;
    private LocalDate currentEndDate;
    private LocalDate newEndDate;
    private int additionalMonths;
    private String reason;

    private RequestStatus status; // PENDING, APPROVED, REJECTED

    @CreatedDate
    private LocalDateTime createdAt;
}

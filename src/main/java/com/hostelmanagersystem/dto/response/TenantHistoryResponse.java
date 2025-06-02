package com.hostelmanagersystem.dto.response;

import com.hostelmanagersystem.enums.TenantStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantHistoryResponse {
    String _id;
    String tenantId;
    String ownerId;
    String roomId;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    TenantStatus status;
}

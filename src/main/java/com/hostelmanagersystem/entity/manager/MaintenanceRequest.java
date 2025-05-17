package com.hostelmanagersystem.entity.manager;

import com.hostelmanagersystem.enums.TenantStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document(value = "maintenance_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaintenanceRequest {
    @MongoId
    String id;
    String tenantId;
    String roomId;
    String description;
    TenantStatus status;
    LocalDate createAt;
}

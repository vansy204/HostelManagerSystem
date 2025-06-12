package com.hostelmanagersystem.entity.manager;
import com.hostelmanagersystem.enums.RequestStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;

@Document("end_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndRequest {
    @MongoId
    String id;

    String tenantId;
    String contractId;
    String reason;
    RequestStatus status;

    LocalDateTime createdAt;
    boolean confirmed;
    LocalDateTime confirmedAt;
}

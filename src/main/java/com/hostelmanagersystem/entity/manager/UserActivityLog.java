package com.hostelmanagersystem.entity.manager;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDateTime;
import java.util.Map;


@Document(collection = "user_activity_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserActivityLog {
    @MongoId
    String id;

    @Indexed
    String userId;

    String userName;

    @Indexed
    String action;

    String description;

    @Indexed
    String ipAddress;

    String userAgent;

    String requestUrl;

    String requestMethod;

    Map<String,Object> requestData;

    Map<String,Object> responseData;

    Integer responseCode;

    String sessionId;

    String deviceInfo;

    String role;

    @Indexed
    LocalDateTime timestamp;

    Long executionTime;
}

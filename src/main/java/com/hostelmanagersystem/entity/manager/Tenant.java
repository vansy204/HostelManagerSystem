package com.hostelmanagersystem.entity.manager;

import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.enums.TenantStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document("tenants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tenant {
    @MongoId
    String id;

    String userId;
    String ownerId; // Chủ trọ quản lý người thuê này
    String roomId;

    String fullName;
    String idCardNumber;
    String phoneNumber;
    String email;
    String note;
    String avatarUrl;


    LocalDate checkInDate;
    LocalDate checkOutDate;

    TenantStatus status;
    LocalDate createAt;
}

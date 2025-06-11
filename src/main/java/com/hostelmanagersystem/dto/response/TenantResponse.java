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
public class TenantResponse {

    String id;

    String userId;
    String ownerId;
    String roomId;

    String fullName;
    String idCardNumber;
    String phoneNumber;
    String email;

    LocalDate checkInDate;
    LocalDate checkOutDate;

    TenantStatus status;
    LocalDate createAt;

}
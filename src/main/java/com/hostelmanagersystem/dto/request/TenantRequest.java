package com.hostelmanagersystem.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TenantRequest {
    String _id;
    String userId;
    String roomId;
    String fullName;
    String phoneNumber;
    String idCardNumber;
    String email;
    String avatarUrl;
    LocalDate checkInDate;
    LocalDate checkOutDate;
}
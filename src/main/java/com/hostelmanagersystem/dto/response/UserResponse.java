package com.hostelmanagersystem.dto.response;

import com.hostelmanagersystem.enums.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.time.LocalDate;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
    String id;
    String userName;
    String email;
    String phone;
    String firstName;
    String lastName;
    boolean isActive;
    Instant createdAt;
    String roleName;
}

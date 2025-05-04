package com.hostelmanagersystem.dto.request;

import com.hostelmanagersystem.enums.RoleEnum;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    String userName;
    String email;
    String phone;
    String password;
    String firstName;
    String lastName;
    String roleName;

}

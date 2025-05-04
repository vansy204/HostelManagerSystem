package com.hostelmanagersystem.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    String userName;
    String email;
    String phone;
    String firstName;
    String lastName;
}

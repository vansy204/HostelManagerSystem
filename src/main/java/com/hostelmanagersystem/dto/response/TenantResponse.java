package com.hostelmanagersystem.dto.response;
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
     String fullName;
     String email;
     String phoneNumber;
     String citizenId;
     LocalDate dateOfBirth;
     String gender;
     String address;
     String roomId;
     LocalDate moveInDate;
     LocalDate moveOutDate;
}

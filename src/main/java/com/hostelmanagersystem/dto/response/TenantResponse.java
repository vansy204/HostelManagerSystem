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
 LocalDate checkInDate;
 LocalDate checkOutDate;
 TenantStatus status;
 LocalDate createAt;
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

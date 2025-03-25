package com.hostelmanagersystem.entity.manager;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.LocalDate;

@Document(value = "tenants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Tenant {
    @MongoId
    String id;
    String userId;
    String roomId;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    Double deposit;
    LocalDate createAt;
}

package com.hostelmanagersystem.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TenantRequest {
     private String userId;
     private String roomId;
     private LocalDate checkInDate;
     private LocalDate checkOutDate;
}
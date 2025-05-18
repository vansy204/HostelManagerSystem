package com.hostelmanagersystem.dto.response;

import com.hostelmanagersystem.enums.TenantStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TenantResponse {
     private String id;
     private String roomId;
     private LocalDate checkInDate;
     private LocalDate checkOutDate;
     private TenantStatus status;
}
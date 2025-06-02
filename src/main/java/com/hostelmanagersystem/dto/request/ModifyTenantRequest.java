package com.hostelmanagersystem.dto.request;

import com.hostelmanagersystem.enums.TenantStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModifyTenantRequest {
    String _id;
    TenantStatus status;
}

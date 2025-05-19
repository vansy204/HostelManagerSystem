package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.UtilityUsageCreateRequest;
import com.hostelmanagersystem.dto.response.UtilityConfigResponse;
import com.hostelmanagersystem.dto.response.UtilityInvoiceResponse;
import com.hostelmanagersystem.dto.response.UtilityUsageResponse;
import com.hostelmanagersystem.entity.manager.UtilityConfig;
import com.hostelmanagersystem.entity.manager.UtilityInvoice;
import com.hostelmanagersystem.entity.manager.UtilityUsage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UtilityMapper {
    UtilityUsage toEntity(UtilityUsageCreateRequest request);
    UtilityUsageResponse toResponse(UtilityUsage entity);
    UtilityInvoiceResponse toUtilityInvoiceResponse(UtilityInvoice invoice);
    UtilityConfigResponse toUtilityConfigResponse(UtilityConfig config);
}

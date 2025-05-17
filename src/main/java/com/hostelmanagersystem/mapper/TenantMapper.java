package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.TenantCreationRequest;
import com.hostelmanagersystem.dto.request.TenantUpdateRequest;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    Tenant toTenant(TenantCreationRequest request);

    void updateTenant(@MappingTarget Tenant tenant, TenantUpdateRequest request);

    TenantResponse toTenantResponse(Tenant tenant);
}

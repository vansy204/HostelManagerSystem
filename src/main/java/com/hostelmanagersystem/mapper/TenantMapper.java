package com.hostelmanagersystem.mapper;
import com.hostelmanagersystem.dto.request.TenantRequest;

import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    TenantResponse toResponse(Tenant tenant);
    Tenant toEntity(TenantRequest request);
    List<TenantResponse> toTenantResponseList(List<Tenant> tenants);
}
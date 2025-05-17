package com.hostelmanagersystem.mapper;


import com.hostelmanagersystem.dto.request.TenantCreationRequest;
import com.hostelmanagersystem.dto.request.TenantUpdateRequest;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "roomId", target = "roomId")
    @Mapping(source = "checkInDate", target = "checkInDate")
    @Mapping(source = "checkOutDate", target = "checkOutDate")
    @Mapping(source = "status", target = "status")
    TenantResponse toTenantResponse(Tenant tenant);

    Tenant toTenant(TenantCreationRequest request);

    void updateTenant(@MappingTarget Tenant tenant, TenantUpdateRequest request);



}

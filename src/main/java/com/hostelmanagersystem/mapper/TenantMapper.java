package com.hostelmanagersystem.mapper;


import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TenantMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "roomId", target = "roomId")
    @Mapping(source = "checkInDate", target = "checkInDate")
    @Mapping(source = "checkOutDate", target = "checkOutDate")
    @Mapping(source = "status", target = "status")
    TenantResponse toTenantResponse(Tenant tenant);

}

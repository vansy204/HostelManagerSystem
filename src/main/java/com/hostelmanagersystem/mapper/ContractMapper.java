package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.ContractCreateRequest;
import com.hostelmanagersystem.dto.request.ContractUpdateRequest;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.entity.manager.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    Contract toEntity(ContractCreateRequest request);
    ContractResponse toResponse(Contract contract);
    void updateContractFromRequest(ContractUpdateRequest request, @MappingTarget Contract contract);
}


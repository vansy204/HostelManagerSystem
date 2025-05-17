package com.hostelmanagersystem.mapper;

import com.hostelmanagersystem.dto.request.ContractCreateRequest;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.entity.manager.Contract;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContractMapper {
    Contract toEntity(ContractCreateRequest request);
    ContractResponse toResponse(Contract contract);
}


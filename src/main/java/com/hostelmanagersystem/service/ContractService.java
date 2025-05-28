package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.ContractCreateRequest;
import com.hostelmanagersystem.dto.request.ContractRenewRequest;
import com.hostelmanagersystem.dto.request.ContractTerminationRequest;
import com.hostelmanagersystem.dto.request.ContractUpdateRequest;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Contract;

import java.util.List;

public interface ContractService {
    ContractResponse createContract(String landlordId, ContractCreateRequest request);
    ContractResponse updateContract(String contractId, ContractUpdateRequest request, String ownerId);

    String generatePdfAndStore(Contract contract);
    void updateContractStatus();

    ContractResponse getContractById(String id, User user);
    ContractResponse renewContract(String contractId, ContractRenewRequest request, String ownerId);
    ContractResponse terminateContract(String contractId, ContractTerminationRequest request, String ownerId);
    List<ContractResponse> getContractsBy(String ownerId);
    List<ContractResponse> getContractsByTenant(String tenantId);
}

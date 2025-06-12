package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.*;
import com.hostelmanagersystem.dto.response.ContractResponse;

import java.util.List;

public interface ContractService {
    ContractResponse createContract(ContractCreateRequest request,String ownerId);
    ContractResponse updateContract(String contractId, ContractUpdateRequest request, String landlordId);
    ContractResponse renewContract(String contractId, ContractRenewRequest request, String landlordId);
    ContractResponse terminateContract(String contractId, ContractTerminationRequest request, String landlordId);
    ContractResponse getContractById(String contractId, String userId);
    List<ContractResponse> getContractsByOwner(String ownerId);
    List<ContractResponse> getContractsByTenant(String tenantId);
    ContractResponse approveContract(String contractId, String ownerId);
    ContractResponse signContract(String contractId);
    ContractResponse cancelContract(String contractId, String ownerId);
    List<ContractResponse> getContractHistoryByTenant(String tenantId, String ownerId);
    List<ContractResponse> searchContracts(ContractFilterRequest request);
    List<ContractResponse> getAllContractsByOwner(String ownerId);
    ContractResponse confirmDepositPayment(String contractId, String ownerId);
    void renewContract(String contractId, int additionalMonths);

}

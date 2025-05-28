package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.ContractCreateRequest;
import com.hostelmanagersystem.dto.response.ContractResponse;
import com.hostelmanagersystem.entity.identity.User;
import com.hostelmanagersystem.entity.manager.Contract;

public interface ContractService {
    ContractResponse createContract(String ownerId, ContractCreateRequest request);
    String generatePdfAndStore(Contract contract);
    void updateContractStatus();

    ContractResponse getContractById(String id, User user);
}

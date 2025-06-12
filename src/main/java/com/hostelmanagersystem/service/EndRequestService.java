package com.hostelmanagersystem.service;

import com.hostelmanagersystem.entity.manager.EndRequest;

import java.util.List;

public interface EndRequestService {
    void createEndRequest(String tenantId, String contractId, String reason);
    void confirmRequest(String requestId);
    List<EndRequest> getAllPendingRequests();
}

package com.hostelmanagersystem.service;

import com.hostelmanagersystem.dto.request.RenewContractRequest;
import com.hostelmanagersystem.entity.RenewRequest;
import com.hostelmanagersystem.enums.RequestStatus;

import java.util.List;

public interface RenewRequestService {
    void createRequest(RenewContractRequest renewContractRequest);
    void approveRequest(String requestId);
    void rejectRequest(String requestId);
    List<RenewRequest> getRequestsByStatus(RequestStatus status);
    List<RenewRequest> getAllRequests(); // nếu muốn admin xem tất cả

}

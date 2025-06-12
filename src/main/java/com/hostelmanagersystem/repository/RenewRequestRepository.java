package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.RenewRequest;
import com.hostelmanagersystem.enums.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RenewRequestRepository extends MongoRepository<RenewRequest, String> {
    List<RenewRequest> findAllByStatus(RequestStatus status);
    List<RenewRequest> findAllByContractId(String contractId);

}

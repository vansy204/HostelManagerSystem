package com.hostelmanagersystem.repository;


import com.hostelmanagersystem.entity.manager.EndRequest;
import com.hostelmanagersystem.enums.RequestStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EndRequestRepository extends MongoRepository<EndRequest, String> {
    List<EndRequest> findByTenantId(String tenantId);
    List<EndRequest> findByStatus(RequestStatus status);
}

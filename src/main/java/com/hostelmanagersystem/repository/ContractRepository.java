package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByLandlordId(String landlordId);
    List<Contract> findByTenantId(String tenantId);
    List<Contract> findByStatus(String status);
}

package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.enums.ContractStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByLandlordId(String landlordId);
    List<Contract> findByTenantId(String tenantId);
    List<Contract> findByStatus(ContractStatus status);
    List<Contract> findByEndDate(LocalDate endDate);

    Optional<Contract> findByRoomIdAndStatus(String roomId, ContractStatus status);
}

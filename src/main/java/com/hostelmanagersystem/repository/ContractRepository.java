package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Contract;
import com.hostelmanagersystem.enums.ContractStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByOwnerId(String ownerId);
    List<Contract> findByTenantId(String tenantId);
    List<Contract> findByStatus(ContractStatus status);
    Optional<Contract> findByRoomId(String roomId);
    List<Contract> findByEndDate(LocalDate endDate);
    List<Contract> findByTenantIdAndOwnerId(String tenantId, String ownerId);


}

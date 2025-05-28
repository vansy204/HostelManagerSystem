package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.UtilityInvoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityInvoiceRepository extends MongoRepository<UtilityInvoice, String> {
    UtilityInvoice save(UtilityInvoice invoice);
    Optional<UtilityInvoice> findById(String id);
    Optional<UtilityInvoice> findByUsageId(String usageId);
    List<UtilityInvoice> findByOwnerIdAndCreatedAtBetween(String ownerId, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}

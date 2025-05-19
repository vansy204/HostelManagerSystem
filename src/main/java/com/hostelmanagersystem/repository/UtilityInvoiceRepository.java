package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.UtilityInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityInvoiceRepository extends MongoRepository<UtilityInvoice, String> {
    UtilityInvoice save(UtilityInvoice invoice);
    Optional<UtilityInvoice> findById(String id);
    List<UtilityInvoice> findByLandlordIdAndMonth(String landlordId, String month);
    Optional<UtilityInvoice> findByUsageId(String usageId);
}

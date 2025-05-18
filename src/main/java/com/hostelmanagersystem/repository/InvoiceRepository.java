package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByTenantId(String tenantId);
    List<Invoice> findByLandlordIdAndMonth(String landlordId, String month);
    Optional<Invoice> findByTenantIdAndMonth(String tenantId, String month);
}

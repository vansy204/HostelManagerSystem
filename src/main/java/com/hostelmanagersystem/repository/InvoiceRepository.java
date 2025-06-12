package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Invoice;
import com.hostelmanagersystem.enums.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByTenantId(String tenantId);
    Invoice findInvoiceByTenantId(String tenantId);
    List<Invoice> findAllByTenantId(String tenantId);
    List<Invoice> findByOwnerIdAndMonth(String ownerId, String month);
    Optional<Invoice> findByTenantIdAndMonth(String tenantId, String month);
    Invoice save(Invoice invoice);
    Optional<Invoice> findById(String id);
    List<Invoice> findByOwnerIdAndRoomIdAndStatus(String ownerId, String roomId, InvoiceStatus status);
    List<Invoice> findByOwnerIdAndCreatedAtBetween(String ownerId, LocalDateTime start,LocalDateTime end);
    Optional<Invoice> findByIdAndOwnerId(String invoiceId,String ownerId);
    List<Invoice> findByOwnerIdAndMonthAndType(String ownerId, String month, String type);
    Page<Invoice> findByOwnerId(String ownerId, Pageable pageable);
    List<Invoice> findByOwnerIdAndStatus(String ownerId,InvoiceStatus status);
}

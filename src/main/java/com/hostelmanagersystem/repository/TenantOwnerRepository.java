package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.dto.response.TenantHistoryResponse;
import com.hostelmanagersystem.dto.response.TenantResponse;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.TenantStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantOwnerRepository extends MongoRepository<Tenant, String> {
    List<Tenant> findByRoomId(String roomId);
    List<Tenant> findByOwnerId(String ownerId);
    List<Tenant> findByOwnerIdAndStatus(String ownerId, TenantStatus status);
    Optional<Tenant> findByIdAndOwnerId(String tenantId, String ownerId);
    List<TenantHistoryResponse> findHistoryByUserId(String userId);
    Optional<Tenant> findById(String id);
    Optional<Tenant> findTenantByRoomId(String roomId);
}

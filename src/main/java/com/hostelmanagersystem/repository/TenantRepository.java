package com.hostelmanagersystem.repository;


import com.hostelmanagersystem.dto.request.TenantRequest;
import com.hostelmanagersystem.dto.response.TenantHistoryResponse;
import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.TenantStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {
    List<Tenant> findByUserId(String userId);
    Optional<Tenant> findByUserIdAndRoomIdAndStatusNot(String userId, String roomId, TenantStatus excludedStatus);
    List<Tenant> findByRoomId(String roomId);
    List<Tenant> findByOwnerIdAndStatus(String ownerId, TenantStatus status);
    Optional<Tenant> findByIdAndOwnerId(String tenantId, String ownerId);
    List<TenantHistoryResponse> findHistoryByUserId(String userId);

}
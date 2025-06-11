package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Tenant;
import com.hostelmanagersystem.enums.TenantStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {
    List<Tenant> findByUserId(String userId);
    Optional<Tenant> findByUserIdAndRoomIdAndStatusNot(String userId, String roomId, TenantStatus excludedStatus);
    Tenant findTenantByUserId(String userId);
    Optional<Tenant> findByUserIdAndRoomId(String userId, String roomId);
    Optional<Tenant> findActiveTenantByRoomId(String roomId);
}
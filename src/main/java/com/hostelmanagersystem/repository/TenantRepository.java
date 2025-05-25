package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Tenant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TenantRepository extends MongoRepository<Tenant, String> {
    List<Tenant> findByUserId(String userId);

}
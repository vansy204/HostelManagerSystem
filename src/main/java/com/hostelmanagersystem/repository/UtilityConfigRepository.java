package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.UtilityConfig;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilityConfigRepository extends MongoRepository<UtilityConfig, String> {
    Optional<UtilityConfig> findByOwnerId(String OwnerId);
    Optional<UtilityConfig> findByLandlordId(String landlordId);
    UtilityConfig save(UtilityConfig config);
}

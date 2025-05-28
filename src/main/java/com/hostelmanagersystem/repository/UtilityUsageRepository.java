package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.UtilityUsage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityUsageRepository extends MongoRepository<UtilityUsage, String> {

    List<UtilityUsage> findByOwnerIdAndMonth(String ownerId, String month);

    Optional<UtilityUsage> findByRoomIdAndMonth(String roomId, String month);
}

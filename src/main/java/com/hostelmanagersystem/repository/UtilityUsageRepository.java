package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.UtilityUsage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilityUsageRepository extends MongoRepository<UtilityUsage, String> {

    List<UtilityUsage> findByOwnerIdAndMonth(String ownerId, String month);

    Optional<UtilityUsage> findByRoomIdAndMonth(String roomId, String month);

    Optional<UtilityUsage> findTopByOwnerIdAndRoomIdOrderByMonthDesc(String ownerId, String roomId);

    Optional<UtilityUsage> findByOwnerIdAndRoomIdAndMonth(String ownerId,String roomId,String previousMonthStr);
}

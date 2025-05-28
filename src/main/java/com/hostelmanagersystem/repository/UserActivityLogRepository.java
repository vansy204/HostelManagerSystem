package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.UserActivityLog;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.Document;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface UserActivityLogRepository extends MongoRepository<UserActivityLog, String> {
    // tim log theo user
    List<UserActivityLog> findByUserIdOrderByTimestampDesc(String userId);
    // tim log theo action
    List<UserActivityLog> findByActionOrderByTimestampDesc(String userId);
    // tim log trong khoang thoi gian
    List<UserActivityLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime startDate, LocalDateTime endDate);

    long countByActionAndTimestampBetween(
            String action, LocalDateTime startTime, LocalDateTime endTime);

    // Custom query với Aggregation
    @Aggregation(pipeline = {
            "{ $match: { timestamp: { $gte: ?0, $lte: ?1 } } }",
            "{ $group: { _id: '$action', count: { $sum: 1 } } }",
            "{ $sort: { count: -1 } }"
    })
    List<Document> getActionStatistics(LocalDateTime startTime, LocalDateTime endTime);
}


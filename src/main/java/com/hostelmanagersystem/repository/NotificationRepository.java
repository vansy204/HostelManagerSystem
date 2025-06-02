package com.hostelmanagersystem.repository;

import com.hostelmanagersystem.entity.manager.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    List<Notification> findBySenderIdOrderByCreatedAtDesc(String senderId);
}

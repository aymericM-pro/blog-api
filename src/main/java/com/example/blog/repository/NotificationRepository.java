package com.example.blog.repository;

import com.example.blog.domain.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(String userId);
    long countByUserIdAndReadFalse(String userId);
    void deleteByUserId(String userId);
    List<Notification> findByUserIdAndReadFalse(String userId);
}

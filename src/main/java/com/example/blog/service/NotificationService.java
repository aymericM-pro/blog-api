package com.example.blog.service;


import com.example.blog.domain.Notification;
import com.example.blog.enums.NotificationType;

import java.util.List;

public interface NotificationService {

    Notification create(String userId, NotificationType type, String title, String message);

    List<Notification> getByUserId(String userId);

    long countUnread(String userId);

    void markAllAsRead(String userId);
}
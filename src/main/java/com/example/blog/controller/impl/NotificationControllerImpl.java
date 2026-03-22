package com.example.blog.controller.impl;

import com.example.blog.controller.NotificationController;
import com.example.blog.security.CustomUserDetailsService;
import com.example.blog.service.NotificationService;
import com.example.blog.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.blog.domain.Notification;

import java.security.Principal;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public SseEmitter stream(Principal principal) {
        String userId = userDetailsService.loadDomainUserByEmail(principal.getName()).getId();
        log.info("SSE stream opened for user {} (email={})", userId, principal.getName());
        SseEmitter emitter = sseEmitterService.register(userId);
        log.debug("SseEmitter registered, returning to client");
        return emitter;
    }

    @Override
    public ResponseEntity<List<Notification>> getAll(Principal principal) {
        String userId = userDetailsService.loadDomainUserByEmail(principal.getName()).getId();
        List<Notification> notifications = notificationService.getByUserId(userId);
        log.info("GET /notifications → {} notification(s) for user {}", notifications.size(), userId);
        return ResponseEntity.ok(notifications);
    }

    @Override
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        String userId = userDetailsService.loadDomainUserByEmail(principal.getName()).getId();
        long unreadBefore = notificationService.countUnread(userId);
        notificationService.markAllAsRead(userId);
        log.info("PATCH /notifications/read → {} notification(s) marked as read for user {}", unreadBefore, userId);
        return ResponseEntity.ok().build();
    }
}
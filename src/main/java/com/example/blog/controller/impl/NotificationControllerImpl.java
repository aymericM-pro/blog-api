package com.example.blog.controller.impl;

import com.example.blog.controller.NotificationController;
import com.example.blog.security.CustomUserDetailsService;
import com.example.blog.service.NotificationService;
import com.example.blog.service.SseEmitterService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.example.blog.domain.Notification;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class NotificationControllerImpl implements NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public SseEmitter stream(Principal principal) {
        String userId = userDetailsService.loadDomainUserByEmail(principal.getName()).getId();
        return sseEmitterService.register(userId);
    }

    @Override
    public ResponseEntity<List<Notification>> getAll(Principal principal) {
        String userId = userDetailsService.loadDomainUserByEmail(principal.getName()).getId();
        return ResponseEntity.ok(notificationService.getByUserId(userId));
    }

    @Override
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        String userId = userDetailsService.loadDomainUserByEmail(principal.getName()).getId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }
}

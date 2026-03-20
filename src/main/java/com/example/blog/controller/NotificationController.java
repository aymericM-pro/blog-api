package com.example.blog.controller;

import com.example.blog.domain.Notification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.security.Principal;
import java.util.List;

@Tag(name = "Notifications", description = "Notification endpoints")
@RequestMapping("/api/v1/notifications")
public interface NotificationController {

    @Operation(summary = "Ouvre une connexion SSE", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    SseEmitter stream(Principal principal);

    @Operation(summary = "Liste les notifications", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    ResponseEntity<List<Notification>> getAll(Principal principal);

    @Operation(summary = "Marquer toutes comme lues", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/read")
    ResponseEntity<Void> markAllAsRead(Principal principal);
}

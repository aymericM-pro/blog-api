package com.example.blog.service;

import com.example.blog.domain.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseEmitterService {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter register(String userId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE); // 1 minute timeout

        sseEmitter.onCompletion(() -> emitters.remove(userId));
        sseEmitter.onTimeout(() -> emitters.remove(userId));
        sseEmitter.onError((e) -> emitters.remove(userId));

        emitters.put(userId, sseEmitter);
        log.debug("SSE emitter registered for user: {}", userId);
        return sseEmitter;
    }

    public void push(String userId, Notification notification) {
        SseEmitter emitter = emitters.get(userId);

        if (emitter == null) {
            return;
        }

        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .data(notification));
        } catch (IOException e) {
            log.error("Error sending SSE to user {}: {}", userId, e.getMessage());
            emitters.remove(userId);
        }
    }
}

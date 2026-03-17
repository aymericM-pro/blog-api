package com.example.blog.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ErrorResponse(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime timestamp,
        int code,
        String message,
        int status,
        String path,
        List<String> errors
) {
    public static ErrorResponse of(int code, String message, int status, String path) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .status(status)
                .path(path)
                .errors(List.of())
                .build();
    }

    public static ErrorResponse of(int code, String message, int status, String path, List<String> errors) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .code(code)
                .message(message)
                .status(status)
                .path(path)
                .errors(errors)
                .build();
    }
}

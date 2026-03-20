package com.example.blog.dto;

import com.example.blog.enums.Role;

import java.time.LocalDateTime;

public class UsersDtos {

    public record UserUpdateRequest(
            String name,
            String avatar,
            Role role
    ) {}

    public record UserResponse(
            String id,
            String email,
            String name,
            String avatar,
            String role,
            LocalDateTime createdAt
    ) {}
}

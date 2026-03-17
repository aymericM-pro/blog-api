package com.example.blog.dto;

import com.example.blog.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank(message = "Name is required")
            @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
            String name,

            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
            String password,

            Role role
    ) {}

    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email must be valid")
            String email,

            @NotBlank(message = "Password is required")
            String password
    ) {}

    public record AuthResponse(
            String token,
            String type,
            String id,
            String email,
            String name,
            String role
    ) {
        public static AuthResponse of(String token, com.example.blog.domain.User user) {
            return new AuthResponse(
                    token,
                    "Bearer",
                    user.getId(),
                    user.getEmail(),
                    user.getName(),
                    user.getRole().name()
            );
        }
    }
}

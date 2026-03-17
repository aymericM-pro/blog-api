package com.example.blog.controller;

import com.example.blog.dto.AuthDtos.AuthResponse;
import com.example.blog.dto.AuthDtos.LoginRequest;
import com.example.blog.dto.AuthDtos.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Auth", description = "Authentication endpoints")
@RequestMapping("/api/v1/auth")
public interface AuthController {

    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request);

    @Operation(summary = "Login")
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request);
}

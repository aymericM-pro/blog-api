package com.example.blog.controller.impl;

import com.example.blog.controller.AuthController;
import com.example.blog.dto.AuthDtos.AuthResponse;
import com.example.blog.dto.AuthDtos.LoginRequest;
import com.example.blog.dto.AuthDtos.RegisterRequest;
import com.example.blog.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthResponse> register(RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}

package com.example.blog.service;

import com.example.blog.dto.AuthDtos.AuthResponse;
import com.example.blog.dto.AuthDtos.LoginRequest;
import com.example.blog.dto.AuthDtos.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

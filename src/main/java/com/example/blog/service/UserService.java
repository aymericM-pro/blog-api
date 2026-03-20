package com.example.blog.service;

import com.example.blog.dto.UsersDtos.UserResponse;
import com.example.blog.dto.UsersDtos.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponse> findAll(Pageable pageable);
    UserResponse findById(String id);
    UserResponse update(String id, UserUpdateRequest request, String currentUserEmail);
    void delete(String id, String currentUserEmail);
}

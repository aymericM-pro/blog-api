package com.example.blog.controller.impl;

import com.example.blog.controller.UserController;
import com.example.blog.dto.UsersDtos.UserResponse;
import com.example.blog.dto.UsersDtos.UserUpdateRequest;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @Override
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(userService.findAll(pageable));
    }

    @Override
    public ResponseEntity<UserResponse> getById(String id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @Override
    public ResponseEntity<UserResponse> update(String id, UserUpdateRequest request, Principal principal) {
        return ResponseEntity.ok(userService.update(id, request, principal.getName()));
    }

    @Override
    public ResponseEntity<Void> delete(String id, Principal principal) {
        userService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}

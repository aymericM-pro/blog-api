package com.example.blog.controller;

import com.example.blog.dto.UsersDtos.UserResponse;
import com.example.blog.dto.UsersDtos.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Users", description = "User endpoints")
@RequestMapping("/api/v1/users")
public interface UserController {

    @Operation(summary = "Get all users")
    @GetMapping
    ResponseEntity<Page<UserResponse>> getAll(Pageable pageable);

    @Operation(summary = "Get user by id")
    @GetMapping("/{id}")
    ResponseEntity<UserResponse> getById(@PathVariable String id);

    @Operation(summary = "Update user")
    @PatchMapping("/{id}")
    ResponseEntity<UserResponse> update(@PathVariable String id, @RequestBody UserUpdateRequest request, Principal principal);

    @Operation(summary = "Delete user")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id, Principal principal);
}

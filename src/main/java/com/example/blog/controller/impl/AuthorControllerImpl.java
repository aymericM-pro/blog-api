package com.example.blog.controller.impl;

import com.example.blog.controller.AuthorController;
import com.example.blog.dto.AuthorsDtos.AuthorRequest;
import com.example.blog.dto.AuthorsDtos.AuthorResponse;
import com.example.blog.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthorControllerImpl implements AuthorController {

    private final AuthorService authorService;

    @Override
    public ResponseEntity<Page<AuthorResponse>> getAll(Pageable pageable) {
        return ResponseEntity.ok(authorService.findAll(pageable));
    }
    @Override
    public ResponseEntity<AuthorResponse> getById(String id) {
        return ResponseEntity.ok(authorService.findById(id));
    }

    @Override
    public ResponseEntity<AuthorResponse> create(AuthorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authorService.create(request));
    }

    @Override
    public ResponseEntity<AuthorResponse> update(String id, AuthorRequest request) {
        return ResponseEntity.ok(authorService.update(id, request));
    }

    @Override
    public ResponseEntity<Void> delete(String id) {
        authorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
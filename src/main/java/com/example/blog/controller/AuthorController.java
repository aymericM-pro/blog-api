package com.example.blog.controller;

import com.example.blog.dto.AuthorsDtos.AuthorRequest;
import com.example.blog.dto.AuthorsDtos.AuthorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Authors", description = "Author endpoints")
@RequestMapping("/api/v1/authors")
public interface AuthorController {

    @Operation(summary = "Get all authors")
    @GetMapping
    public ResponseEntity<Page<AuthorResponse>> getAll(Pageable pageable);

    @Operation(summary = "Get author by id")
    @GetMapping("/{id}")
    ResponseEntity<AuthorResponse> getById(@PathVariable String id);

    @Operation(summary = "Create author")
    @PostMapping
    ResponseEntity<AuthorResponse> create(@Valid @RequestBody AuthorRequest request);

    @Operation(summary = "Update author")
    @PutMapping("/{id}")
    ResponseEntity<AuthorResponse> update(@PathVariable String id, @Valid @RequestBody AuthorRequest request);

    @Operation(summary = "Delete author")
    @DeleteMapping("/{id}")
    ResponseEntity<Void> delete(@PathVariable String id);
}
package com.example.blog.service;

import com.example.blog.dto.AuthorsDtos.AuthorRequest;
import com.example.blog.dto.AuthorsDtos.AuthorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AuthorService {

    AuthorResponse create(AuthorRequest request);
    Page<AuthorResponse> findAll(Pageable pageable);
    AuthorResponse findById(String id);
    AuthorResponse update(String id, AuthorRequest request);
    void delete(String id);
}
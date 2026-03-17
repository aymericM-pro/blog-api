package com.example.blog.service.impl;

import com.example.blog.domain.Author;
import com.example.blog.dto.AuthorsDtos.AuthorRequest;
import com.example.blog.dto.AuthorsDtos.AuthorResponse;
import com.example.blog.enums.AuthorError;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.AuthorMapper;
import com.example.blog.repository.AuthorRepository;
import com.example.blog.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper mapper;

    // CREATE
    @Override
    public AuthorResponse create(AuthorRequest request) {
        Author author = mapper.toEntity(request);
        return mapper.toResponse(authorRepository.save(author));
    }

    // READ ALL
    @Override
    public Page<AuthorResponse> findAll(Pageable pageable) {
        return authorRepository.findAll(pageable)
                .map(mapper::toResponse);
    }

    // READ ONE
    @Override
    public AuthorResponse findById(String id) {
        return mapper.toResponse(findAuthorById(id));
    }

    // UPDATE
    @Override
    public AuthorResponse update(String id, AuthorRequest request) {
        Author author = findAuthorById(id);

        mapper.updateAuthorFromDto(request, author);

        return mapper.toResponse(authorRepository.save(author));
    }

    // DELETE
    @Override
    public void delete(String id) {
        Author author = findAuthorById(id);
        authorRepository.delete(author);
    }

    // ─── HELPERS ─────────────────────────────────────────

    private Author findAuthorById(String id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new BusinessException(AuthorError.AUTHOR_NOT_FOUND));
    }
}
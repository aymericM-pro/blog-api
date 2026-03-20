package com.example.blog.repository;

import com.example.blog.domain.Author;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AuthorRepository extends MongoRepository<Author, String> {
    Optional<Author> findByUserId(String userId);
}

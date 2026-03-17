package com.example.blog.repository;

import com.example.blog.domain.Article;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {

    Optional<Article> findBySlug(String slug);

    boolean existsBySlug(String slug);
    boolean existsByTitle(String title);
    boolean existsByTitleAndIdNot(String title, String id);
}

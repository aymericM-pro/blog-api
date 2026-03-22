package com.example.blog.repository;

import com.example.blog.domain.Review;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReviewRepository extends MongoRepository<Review, String> {
    List<Review> findByArticleSlugOrderByCreatedAtDesc(String articleSlug);
    boolean existsByArticleIdAndReviewerUserId(String articleId, String reviewerUserId);
}

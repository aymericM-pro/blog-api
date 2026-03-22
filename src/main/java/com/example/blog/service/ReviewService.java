package com.example.blog.service;

import com.example.blog.dto.ReviewDtos;

import java.util.List;

public interface ReviewService {
    List<ReviewDtos.ReviewResponse> getByArticleSlug(String slug);
    ReviewDtos.ReviewResponse createReview(String slug, ReviewDtos.ReviewRequest request, String userEmail);
    void deleteReview(String reviewId, String userEmail);
}

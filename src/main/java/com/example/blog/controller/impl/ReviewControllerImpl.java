package com.example.blog.controller.impl;

import com.example.blog.controller.ReviewController;
import com.example.blog.dto.ReviewDtos;
import com.example.blog.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @Override
    public ResponseEntity<List<ReviewDtos.ReviewResponse>> getReviews(String slug) {
        return ResponseEntity.ok(reviewService.getByArticleSlug(slug));
    }

    @Override
    public ResponseEntity<ReviewDtos.ReviewResponse> createReview(
            String slug, ReviewDtos.ReviewRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(slug, request, principal.getName()));
    }

    @Override
    public ResponseEntity<Void> deleteReview(String slug, String reviewId, Principal principal) {
        reviewService.deleteReview(reviewId, principal.getName());
        return ResponseEntity.noContent().build();
    }
}

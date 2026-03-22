package com.example.blog.controller;

import com.example.blog.dto.ReviewDtos;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Tag(name = "Reviews", description = "Article review endpoints")
@RequestMapping("/api/v1/articles/{slug}/reviews")
public interface ReviewController {

    @Operation(summary = "List reviews for an article")
    @GetMapping
    ResponseEntity<List<ReviewDtos.ReviewResponse>> getReviews(@PathVariable String slug);

    @Operation(summary = "Post a review on an article", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    ResponseEntity<ReviewDtos.ReviewResponse> createReview(
            @PathVariable String slug,
            @Valid @RequestBody ReviewDtos.ReviewRequest request,
            Principal principal
    );

    @Operation(summary = "Delete a review", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{reviewId}")
    ResponseEntity<Void> deleteReview(
            @PathVariable String slug,
            @PathVariable String reviewId,
            Principal principal
    );
}

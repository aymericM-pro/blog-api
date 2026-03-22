package com.example.blog.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class ReviewDtos {

    public record ReviewRequest(
            @NotNull(message = "Rating is required")
            @Min(value = 1, message = "Rating must be at least 1")
            @Max(value = 5, message = "Rating must be at most 5")
            Integer rating,

            @NotBlank(message = "Content is required")
            @Size(max = 2000, message = "Content must not exceed 2000 characters")
            String content
    ) {}

    public record ReviewResponse(
            String id,
            ArticleDtos.AuthorResponse author,
            int rating,
            String content,
            LocalDateTime publishedAt
    ) {}
}

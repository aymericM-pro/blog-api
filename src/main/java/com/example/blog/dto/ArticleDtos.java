package com.example.blog.dto;

import com.example.blog.enums.ArticleCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleDtos {

    public record ArticleRequest(
            @NotBlank(message = "Title is required")
            @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
            String title,

            @NotBlank(message = "Excerpt is required")
            @Size(max = 500, message = "Excerpt must not exceed 500 characters")
            String excerpt,

            @NotBlank(message = "Content is required")
            String content,

            @NotNull(message = "Category is required")
            ArticleCategory category,

            List<String> tags,
            String coverImage,
            boolean published
    ) {}

    public record AuthorResponse(
            String id,
            String name,
            String avatar,
            String role
    ) {}

    public record ArticleResponse(
            String id,
            String slug,
            String title,
            String excerpt,
            String content,
            String category,
            List<String> tags,
            AuthorResponse author,
            LocalDateTime publishedAt,
            int readTime,
            long viewCount,
            String coverImage,
            boolean published,
            boolean isBookmarked,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    public record ArticleSummaryResponse(
            String id,
            String slug,
            String title,
            String excerpt,
            String category,
            List<String> tags,
            AuthorResponse author,
            LocalDateTime publishedAt,
            int readTime,
            long viewCount,
            String coverImage,
            boolean published,
            boolean isBookmarked,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}
}

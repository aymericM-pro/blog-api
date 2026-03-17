package com.example.blog.service;

import com.example.blog.dto.ArticleDtos.ArticleRequest;
import com.example.blog.dto.ArticleDtos.ArticleResponse;
import com.example.blog.dto.ArticleDtos.ArticleSummaryResponse;
import com.example.blog.enums.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {

    Page<ArticleSummaryResponse> getArticles(
            ArticleCategory category,
            String tag,
            String authorId,
            Boolean published,
            String search,
            Pageable pageable,
            String currentUserEmail
    );

    ArticleResponse getArticleById(String id, String currentUserEmail);

    ArticleResponse getArticleBySlug(String slug, String currentUserEmail);

    ArticleResponse createArticle(ArticleRequest request, String authorEmail);

    ArticleResponse updateArticle(String id, ArticleRequest request, String currentUserEmail);

    void deleteArticle(String id, String currentUserEmail);

    ArticleResponse toggleBookmark(String id, String currentUserEmail);
}

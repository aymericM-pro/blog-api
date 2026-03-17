package com.example.blog.controller.impl;

import com.example.blog.controller.ArticleController;
import com.example.blog.dto.ArticleDtos.ArticleRequest;
import com.example.blog.dto.ArticleDtos.ArticleResponse;
import com.example.blog.dto.ArticleDtos.ArticleSummaryResponse;
import com.example.blog.enums.ArticleCategory;
import com.example.blog.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class ArticleControllerImpl implements ArticleController {

    private final ArticleService articleService;

    @Override
    public ResponseEntity<Page<ArticleSummaryResponse>> getArticles(
            ArticleCategory category, String tag, String author, Boolean published,
            String search, Pageable pageable, Principal principal) {

        String email = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(
                articleService.getArticles(category, tag, author, published, search, pageable, email)
        );
    }

    @Override
    public ResponseEntity<ArticleResponse> getArticleById(String id, Principal principal) {
        String email = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(articleService.getArticleById(id, email));
    }

    @Override
    public ResponseEntity<ArticleResponse> getArticleBySlug(String slug, Principal principal) {
        String email = principal != null ? principal.getName() : null;
        return ResponseEntity.ok(articleService.getArticleBySlug(slug, email));
    }

    @Override
    public ResponseEntity<ArticleResponse> createArticle(ArticleRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(articleService.createArticle(request, principal.getName()));
    }

    @Override
    public ResponseEntity<ArticleResponse> updateArticle(String id, ArticleRequest request, Principal principal) {
        return ResponseEntity.ok(articleService.updateArticle(id, request, principal.getName()));
    }

    @Override
    public ResponseEntity<Void> deleteArticle(String id, Principal principal) {
        articleService.deleteArticle(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ArticleResponse> toggleBookmark(String id, Principal principal) {
        return ResponseEntity.ok(articleService.toggleBookmark(id, principal.getName()));
    }
}

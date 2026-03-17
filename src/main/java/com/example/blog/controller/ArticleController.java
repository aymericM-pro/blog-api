package com.example.blog.controller;

import com.example.blog.dto.ArticleDtos.ArticleRequest;
import com.example.blog.dto.ArticleDtos.ArticleResponse;
import com.example.blog.dto.ArticleDtos.ArticleSummaryResponse;
import com.example.blog.enums.ArticleCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "Articles", description = "Blog article endpoints")
@RequestMapping("/api/v1/articles")
public interface ArticleController {

    @Operation(summary = "List articles with filters and pagination")
    @GetMapping
    ResponseEntity<Page<ArticleSummaryResponse>> getArticles(
            @RequestParam(required = false) ArticleCategory category,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20) Pageable pageable,
            Principal principal
    );

    @Operation(summary = "Get article by ID (increments viewCount)")
    @GetMapping("/{id}")
    ResponseEntity<ArticleResponse> getArticleById(@PathVariable String id, Principal principal);

    @Operation(summary = "Get article by slug (increments viewCount)")
    @GetMapping("/slug/{slug}")
    ResponseEntity<ArticleResponse> getArticleBySlug(@PathVariable String slug, Principal principal);

    @Operation(summary = "Create an article", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    ResponseEntity<ArticleResponse> createArticle(@Valid @RequestBody ArticleRequest request, Principal principal);

    @Operation(summary = "Update an article", security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/{id}")
    ResponseEntity<ArticleResponse> updateArticle(
            @PathVariable String id,
            @Valid @RequestBody ArticleRequest request,
            Principal principal
    );

    @Operation(summary = "Delete an article", security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteArticle(@PathVariable String id, Principal principal);

    @Operation(summary = "Toggle bookmark on an article", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/{id}/bookmark")
    ResponseEntity<ArticleResponse> toggleBookmark(@PathVariable String id, Principal principal);
}

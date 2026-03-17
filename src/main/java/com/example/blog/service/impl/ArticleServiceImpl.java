package com.example.blog.service.impl;

import com.example.blog.domain.Article;
import com.example.blog.domain.Author;
import com.example.blog.domain.User;
import com.example.blog.dto.ArticleDtos.*;
import com.example.blog.enums.ArticleCategory;
import com.example.blog.enums.ArticleError;
import com.example.blog.enums.AuthError;
import com.example.blog.enums.Role;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.ArticleService;
import com.github.slugify.Slugify;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ArticleMapper articleMapper;
    private final UserMapper userMapper;
    private final MongoTemplate mongoTemplate;
    private final Slugify slugify = Slugify.builder().build();

    // ─── READ ────────────────────────────────────────────────────────────────

    @Override
    public Page<ArticleSummaryResponse> getArticles(
            ArticleCategory category, String tag, String authorId,
            Boolean published, String search, Pageable pageable, String currentUserEmail) {

        Criteria criteria = new Criteria();
        List<Criteria> conditions = new ArrayList<>();

        if (category != null) conditions.add(Criteria.where("category").is(category));
        if (tag != null && !tag.isBlank()) conditions.add(Criteria.where("tags").in(tag));
        if (authorId != null && !authorId.isBlank()) conditions.add(Criteria.where("author.id").is(authorId));
        if (published != null) conditions.add(Criteria.where("published").is(published));
        if (search != null && !search.isBlank()) {
            conditions.add(new Criteria().orOperator(
                    Criteria.where("title").regex(search, "i"),
                    Criteria.where("excerpt").regex(search, "i")
            ));
        }

        if (!conditions.isEmpty()) {
            criteria.andOperator(conditions.toArray(new Criteria[0]));
        }

        Query query = new Query(criteria).with(pageable);
        List<Article> articles = mongoTemplate.find(query, Article.class);
        long count = mongoTemplate.count(Query.of(query).limit(-1).skip(-1), Article.class);

        User currentUser = resolveUser(currentUserEmail);

        return PageableExecutionUtils.getPage(
                articles.stream()
                        .map(a -> toSummaryWithBookmark(a, currentUser))
                        .toList(),
                pageable,
                () -> count
        );
    }

    @Override
    public ArticleResponse getArticleById(String id, String currentUserEmail) {
        Article article = findArticleById(id);
        article.setViewCount((article.getViewCount() != null ? article.getViewCount() : 0L) + 1);
        articleRepository.save(article);
        return toResponseWithBookmark(article, resolveUser(currentUserEmail));
    }

    @Override
    public ArticleResponse getArticleBySlug(String slug, String currentUserEmail) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ArticleError.ARTICLE_NOT_FOUND));
        article.setViewCount((article.getViewCount() != null ? article.getViewCount() : 0L) + 1);
        articleRepository.save(article);
        return toResponseWithBookmark(article, resolveUser(currentUserEmail));
    }

    // ─── CREATE ──────────────────────────────────────────────────────────────

    @Override
    public ArticleResponse createArticle(ArticleRequest request, String authorEmail) {
        if (articleRepository.existsByTitle(request.title())) {
            throw new BusinessException(ArticleError.ARTICLE_TITLE_ALREADY_EXISTS);
        }

        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new BusinessException(AuthError.USER_NOT_FOUND));

        Article article = articleMapper.toEntity(request);
        article.setSlug(generateUniqueSlug(request.title()));
        article.setAuthorId(author.getId());
        article.setAuthor(userMapper.toAuthor(author));
        article.setReadTime(calculateReadTime(request.content()));

        if (request.published()) {
            article.setPublishedAt(LocalDateTime.now());
        }

        return toResponseWithBookmark(articleRepository.save(article), null);
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    @Override
    public ArticleResponse updateArticle(String id, ArticleRequest request, String currentUserEmail) {
        Article article = findArticleById(id);
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new BusinessException(com.example.blog.enums.AuthError.USER_NOT_FOUND));

        assertCanModify(article, currentUser);

        if (!article.getTitle().equals(request.title())
                && articleRepository.existsByTitleAndIdNot(request.title(), id)) {
            throw new BusinessException(ArticleError.ARTICLE_TITLE_ALREADY_EXISTS);
        }

        boolean wasPublished = article.isPublished();
        articleMapper.updateEntityFromRequest(request, article);

        if (!article.getTitle().equals(request.title())) {
            article.setSlug(generateUniqueSlug(request.title()));
        }
        article.setReadTime(calculateReadTime(request.content()));

        if (request.published() && !wasPublished) {
            article.setPublishedAt(LocalDateTime.now());
        }

        return toResponseWithBookmark(articleRepository.save(article), currentUser);
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    @Override
    public void deleteArticle(String id, String currentUserEmail) {
        Article article = findArticleById(id);
        User currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new BusinessException(AuthError.USER_NOT_FOUND));
        assertCanModify(article, currentUser);
        articleRepository.delete(article);
    }

    // ─── BOOKMARK ────────────────────────────────────────────────────────────

    @Override
    public ArticleResponse toggleBookmark(String id, String currentUserEmail) {
        Article article = findArticleById(id);
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new BusinessException(AuthError.USER_NOT_FOUND));

        List<String> bookmarks = new ArrayList<>(user.getBookmarkedArticleIds());

        if (bookmarks.contains(id)) {
            bookmarks.remove(id);
        } else {
            bookmarks.add(id);
        }

        user.setBookmarkedArticleIds(bookmarks);
        userRepository.save(user);

        return toResponseWithBookmark(article, user);
    }

    // ─── HELPERS ─────────────────────────────────────────────────────────────

    private Article findArticleById(String id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ArticleError.ARTICLE_NOT_FOUND));
    }

    private void assertCanModify(Article article, User user) {
        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isAuthor = article.getAuthorId() != null
                && article.getAuthorId().equals(user.getId());
        if (!isAdmin && !isAuthor) {
            throw new BusinessException(ArticleError.ARTICLE_FORBIDDEN);
        }
    }

    private String generateUniqueSlug(String title) {
        String base = slugify.slugify(title);
        if (!articleRepository.existsBySlug(base)) return base;
        int i = 2;
        while (articleRepository.existsBySlug(base + "-" + i)) i++;
        return base + "-" + i;
    }

    private int calculateReadTime(String content) {
        if (content == null || content.isBlank()) return 1;
        int wordCount = content.split("\\s+").length;
        return Math.max(1, wordCount / 200);
    }

    private User resolveUser(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email).orElse(null);
    }

    private ArticleResponse toResponseWithBookmark(Article article, User user) {
        ArticleResponse base = articleMapper.toResponse(article);
        boolean bookmarked = user != null && user.getBookmarkedArticleIds().contains(article.getId());
        return new ArticleResponse(
                base.id(), base.slug(), base.title(), base.excerpt(), base.content(),
                base.category(), base.tags(), base.author(), base.publishedAt(),
                base.readTime(), base.viewCount(), base.coverImage(), base.published(),
                bookmarked, base.createdAt(), base.updatedAt()
        );
    }

    private ArticleSummaryResponse toSummaryWithBookmark(Article article, User user) {
        ArticleSummaryResponse base = articleMapper.toSummaryResponse(article);
        boolean bookmarked = user != null && user.getBookmarkedArticleIds().contains(article.getId());
        return new ArticleSummaryResponse(
                base.id(), base.slug(), base.title(), base.excerpt(),
                base.category(), base.tags(), base.author(), base.publishedAt(),
                base.readTime(), base.viewCount(), base.coverImage(), base.published(),
                bookmarked, base.createdAt(), base.updatedAt()
        );
    }
}

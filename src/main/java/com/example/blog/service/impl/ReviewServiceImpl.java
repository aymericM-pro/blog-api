package com.example.blog.service.impl;

import com.example.blog.domain.Article;
import com.example.blog.domain.Author;
import com.example.blog.domain.Review;
import com.example.blog.domain.User;
import com.example.blog.dto.ReviewDtos;
import com.example.blog.enums.ArticleError;
import com.example.blog.enums.AuthError;
import com.example.blog.enums.ReviewError;
import com.example.blog.enums.Role;
import com.example.blog.event.ReviewCreatedEvent;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.ReviewMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.ReviewRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public List<ReviewDtos.ReviewResponse> getByArticleSlug(String slug) {
        articleRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ArticleError.ARTICLE_NOT_FOUND));

        return reviewRepository.findByArticleSlugOrderByCreatedAtDesc(slug)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Override
    public ReviewDtos.ReviewResponse createReview(String slug, ReviewDtos.ReviewRequest request, String userEmail) {
        Article article = articleRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(ArticleError.ARTICLE_NOT_FOUND));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(AuthError.USER_NOT_FOUND));

        if (reviewRepository.existsByArticleIdAndReviewerUserId(article.getId(), user.getId())) {
            throw new BusinessException(ReviewError.REVIEW_ALREADY_EXISTS);
        }

        Author authorSnapshot = userMapper.toAuthor(user);

        Review review = reviewMapper.toEntity(request);
        review.setArticleId(article.getId());
        review.setArticleSlug(article.getSlug());
        review.setReviewerUserId(user.getId());
        review.setAuthor(authorSnapshot);

        Review saved = reviewRepository.save(review);
        log.info("Review created: id={}, articleSlug={}, userId={}", saved.getId(), slug, user.getId());

        eventPublisher.publishEvent(new ReviewCreatedEvent(this, saved, article));

        return reviewMapper.toResponse(saved);
    }

    @Override
    public void deleteReview(String reviewId, String userEmail) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException(ReviewError.REVIEW_NOT_FOUND));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BusinessException(AuthError.USER_NOT_FOUND));

        boolean isOwner = review.getReviewerUserId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new BusinessException(ReviewError.REVIEW_FORBIDDEN);
        }

        reviewRepository.delete(review);
        log.info("Review deleted: id={}, deletedBy={}", reviewId, userEmail);
    }
}

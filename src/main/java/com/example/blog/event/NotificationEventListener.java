package com.example.blog.event;

import com.example.blog.domain.Article;
import com.example.blog.domain.Notification;
import com.example.blog.domain.Review;
import com.example.blog.domain.User;
import com.example.blog.enums.NotificationType;
import com.example.blog.repository.AuthorRepository;
import com.example.blog.service.NotificationService;
import com.example.blog.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final SseEmitterService sseEmitterService;
    private final AuthorRepository authorRepository;

    @EventListener
    public void onArticlePublished(ArticlePublishedEvent event) {
        Article article = event.getArticle();
        log.info("Event received: article '{}' published (authorId={})", article.getTitle(), article.getAuthorId());

        // authorId = ID du doc Author, pas du doc User
        // il faut résoudre le userId réel
        String userId = authorRepository.findById(article.getAuthorId())
                .map(author -> author.getUserId())
                .orElse(null);

        if (userId == null) {
            log.warn("Cannot find author {} — notification skipped", article.getAuthorId());
            return;
        }

        Notification notification = notificationService.create(
                userId,  // ← userId réel, pas authorId
                NotificationType.ARTICLE_PUBLISHED,
                "New article published: " + article.getTitle(),
                "\"" + article.getTitle() + "\" has been published."
        );

        log.info("Notification created: id={}, userId={}", notification.getId(), notification.getUserId());
        sseEmitterService.push(userId, notification);  // ← aussi ici
    }

    @EventListener
    public void onUserRegistered(UserRegisteredEvent event) {
        User user = event.getUser();
        log.info("Event received: user '{}' registered (userId={})", user.getEmail(), user.getId());

        Notification notification = notificationService.create(
                user.getId(),
                NotificationType.WELCOME,
                "Bienvenue sur Tokyo Night Blog !",
                "Bonjour " + user.getName() + ", votre compte a bien été créé. Bonne lecture !"
        );

        log.info("Welcome notification created: id={}, userId={}", notification.getId(), notification.getUserId());
    }

    @EventListener
    public void onReviewCreated(ReviewCreatedEvent event) {
        Review review = event.getReview();
        Article article = event.getArticle();
        log.info("Event received: review created on article '{}' (authorId={})", article.getTitle(), article.getAuthorId());

        String userId = authorRepository.findById(article.getAuthorId())
                .map(author -> author.getUserId())
                .orElse(null);

        if (userId == null) {
            log.warn("Cannot find author {} — review notification skipped", article.getAuthorId());
            return;
        }

        Notification notification = notificationService.create(
                userId,
                NotificationType.NEW_REVIEW,
                "New review on your article",
                "\"" + article.getTitle() + "\" received a new " + review.getRating() + "-star review."
        );

        log.info("Review notification created: id={}, userId={}", notification.getId(), notification.getUserId());
        sseEmitterService.push(userId, notification);
    }
}
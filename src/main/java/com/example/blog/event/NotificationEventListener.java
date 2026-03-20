package com.example.blog.event;

import com.example.blog.domain.Article;
import com.example.blog.domain.Notification;
import com.example.blog.enums.NotificationType;
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

    @EventListener
    public void onArticlePublished(ArticlePublishedEvent event) {
        Article article = event.getArticle();
        log.info("Event received: article '{}' published (authorId={})", article.getTitle(), article.getAuthorId());

        Notification notification = notificationService.create(
                article.getAuthorId(),
                NotificationType.ARTICLE_PUBLISHED,
                "New article published: " + article.getTitle(),
                "\"" + article.getTitle() + "\" has been published."
        );

        log.info("Notification created: id={}, userId={}", notification.getId(), notification.getUserId());
        sseEmitterService.push(article.getAuthorId(), notification);
    }
}

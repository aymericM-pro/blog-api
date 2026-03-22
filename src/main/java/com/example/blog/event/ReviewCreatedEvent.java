package com.example.blog.event;

import com.example.blog.domain.Article;
import com.example.blog.domain.Review;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReviewCreatedEvent extends ApplicationEvent {

    private final Review review;
    private final Article article;

    public ReviewCreatedEvent(Object source, Review review, Article article) {
        super(source);
        this.review = review;
        this.article = article;
    }
}

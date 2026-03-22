package com.example.blog.event;

import com.example.blog.domain.Article;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ArticlePublishedEvent extends ApplicationEvent
{
    private final Article article;

    public ArticlePublishedEvent(Object source, Article article) {
        super(source);
        this.article = article;
    }
}

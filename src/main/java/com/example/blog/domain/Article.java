package com.example.blog.domain;

import com.example.blog.enums.ArticleCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "articles")
public class Article {

    @Id
    private String id;

    @Indexed(unique = true)
    private String slug;

    @Indexed
    private String title;

    private String excerpt;
    private String content;

    @Indexed
    private ArticleCategory category;

    @Builder.Default
    private List<String> tags = new ArrayList<>();


    @Indexed
    private String authorId;

    private Author author;

    private LocalDateTime publishedAt;
    private Integer readTime;

    private Long viewCount;

    private String coverImage;

    @Builder.Default
    private boolean published = false;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}

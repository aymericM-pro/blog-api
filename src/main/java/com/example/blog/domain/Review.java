package com.example.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndex(
        name = "unique_review_per_user_article",
        def = "{'articleId': 1, 'reviewerUserId': 1}",
        unique = true
)
public class Review {

    @Id
    private String id;

    @Indexed
    private String articleId;

    @Indexed
    private String articleSlug;

    private String reviewerUserId;

    private Author author;

    private int rating;

    private String content;

    @CreatedDate
    private LocalDateTime createdAt;
}

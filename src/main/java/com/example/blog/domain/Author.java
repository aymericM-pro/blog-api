package com.example.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "authors")
public class Author {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;
    private String avatar;
    private String role;

    @CreatedDate
    private String createdAt;
}

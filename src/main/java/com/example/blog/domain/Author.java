package com.example.blog.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "authors")
public class Author {

    @Id
    private String id;

    private String userId;

    @Indexed(unique = true)
    private String name;
    private String avatar;
    private String role;
    private String description;

    private String location;
    private String website;
    private String twitter;
    private String github;
    private String linkedin;

    @Builder.Default
    private List<String> skills = new ArrayList<>();

    @CreatedDate
    private String createdAt;
}

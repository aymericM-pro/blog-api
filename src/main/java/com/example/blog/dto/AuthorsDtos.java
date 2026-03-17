package com.example.blog.dto;

import com.example.blog.domain.Author;

public class AuthorsDtos {

    public record AuthorRequest(
            String name,
            String avatar,
            String role
    ) {}

    public record AuthorResponse(
            String id,
            String name,
            String avatar,
            String role,
            String createdAt
    ) {
        public static AuthorResponse of(Author author) {
            return new AuthorResponse(
                    author.getId(),
                    author.getName(),
                    author.getAvatar(),
                    author.getRole(),
                    author.getCreatedAt()
            );
        }
    }
}

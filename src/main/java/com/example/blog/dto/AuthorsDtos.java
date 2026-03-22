package com.example.blog.dto;

import com.example.blog.domain.Author;

import java.util.List;

public class AuthorsDtos {

    public record AuthorRequest(
            String name,
            String avatar,
            String role,
            String description,
            String location,
            String website,
            String twitter,
            String github,
            String linkedin,
            List<String> skills
    ) {}

    public record AuthorResponse(
            String id,
            String name,
            String avatar,
            String role,
            String description,
            String location,
            String website,
            String twitter,
            String github,
            String linkedin,
            List<String> skills,
            String createdAt
    ) {
        public static AuthorResponse of(Author author) {
            return new AuthorResponse(
                    author.getId(),
                    author.getName(),
                    author.getAvatar(),
                    author.getRole(),
                    author.getDescription(),
                    author.getLocation(),
                    author.getWebsite(),
                    author.getTwitter(),
                    author.getGithub(),
                    author.getLinkedin(),
                    author.getSkills(),
                    author.getCreatedAt()
            );
        }
    }
}

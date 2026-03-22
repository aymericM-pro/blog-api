package com.example.blog.mapper;

import com.example.blog.domain.Author;
import com.example.blog.domain.Review;
import com.example.blog.dto.ArticleDtos;
import com.example.blog.dto.ReviewDtos;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReviewMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "articleId", ignore = true)
    @Mapping(target = "articleSlug", ignore = true)
    @Mapping(target = "reviewerUserId", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Review toEntity(ReviewDtos.ReviewRequest request);

    @Mapping(target = "publishedAt", source = "createdAt")
    ReviewDtos.ReviewResponse toResponse(Review review);

    ArticleDtos.AuthorResponse toAuthorResponse(Author author);
}

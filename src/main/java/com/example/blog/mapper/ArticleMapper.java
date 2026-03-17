package com.example.blog.mapper;

import com.example.blog.domain.Article;
import com.example.blog.domain.Author;
import com.example.blog.dto.ArticleDtos.ArticleRequest;
import com.example.blog.dto.ArticleDtos.ArticleResponse;
import com.example.blog.dto.ArticleDtos.ArticleSummaryResponse;
import com.example.blog.dto.ArticleDtos.AuthorResponse;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ArticleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "readTime", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Article toEntity(ArticleRequest request);

    @Mapping(target = "category", expression = "java(article.getCategory() != null ? article.getCategory().getValue() : null)")
    @Mapping(target = "isBookmarked", ignore = true)
    ArticleResponse toResponse(Article article);

    @Mapping(target = "category", expression = "java(article.getCategory() != null ? article.getCategory().getValue() : null)")
    @Mapping(target = "isBookmarked", ignore = true)
    ArticleSummaryResponse toSummaryResponse(Article article);

    AuthorResponse toAuthorResponse(Author author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "slug", ignore = true)
    @Mapping(target = "authorId", ignore = true)
    @Mapping(target = "publishedAt", ignore = true)
    @Mapping(target = "readTime", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ArticleRequest request, @MappingTarget Article article);
}

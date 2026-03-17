package com.example.blog.mapper;

import com.example.blog.domain.Article;
import com.example.blog.domain.Author;
import com.example.blog.domain.User;
import com.example.blog.dto.ArticleDtos.ArticleRequest;
import com.example.blog.dto.ArticleDtos.ArticleResponse;
import com.example.blog.dto.ArticleDtos.ArticleSummaryResponse;
import com.example.blog.enums.ArticleCategory;
import com.example.blog.enums.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ArticleMapper — Tests unitaires MapStruct")
class ArticleMapperTest {

    private final ArticleMapper articleMapper = Mappers.getMapper(ArticleMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    @DisplayName("toEntity : doit mapper ArticleRequest → Article correctement")
    void shouldMapRequestToEntity() {
        ArticleRequest request = new ArticleRequest(
                "My Title",
                "My excerpt",
                "My content",
                ArticleCategory.DEV,
                List.of("java", "spring"),
                "https://cover.jpg",
                true
        );

        Article article = articleMapper.toEntity(request);

        assertThat(article.getTitle()).isEqualTo("My Title");
        assertThat(article.getExcerpt()).isEqualTo("My excerpt");
        assertThat(article.getContent()).isEqualTo("My content");
        assertThat(article.getCategory()).isEqualTo(ArticleCategory.DEV);
        assertThat(article.getTags()).containsExactly("java", "spring");
        assertThat(article.getCoverImage()).isEqualTo("https://cover.jpg");
        assertThat(article.isPublished()).isTrue();

        // Ces champs doivent être ignorés par le mapper
        assertThat(article.getId()).isNull();
        assertThat(article.getSlug()).isNull();
        assertThat(article.getAuthorId()).isNull();
        assertThat(article.getPublishedAt()).isNull();
        assertThat(article.getReadTime()).isNull();
        assertThat(article.getViewCount()).isNull();
        assertThat(article.getCreatedAt()).isNull();
        assertThat(article.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("toResponse : doit mapper Article → ArticleResponse avec category en String")
    void shouldMapArticleToResponse() {
        Author author = Author.builder()
                .id("user-1")
                .name("John Doe")
                .avatar("https://avatar.jpg")
                .role("AUTHOR")
                .build();

        Article article = Article.builder()
                .id("article-1")
                .slug("my-title")
                .title("My Title")
                .excerpt("My excerpt")
                .content("My content")
                .category(ArticleCategory.DEV)
                .tags(List.of("java"))
                .authorId("user-1")
                .author(author)
                .published(true)
                .viewCount(5L)
                .readTime(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ArticleResponse response = articleMapper.toResponse(article);

        assertThat(response.id()).isEqualTo("article-1");
        assertThat(response.slug()).isEqualTo("my-title");
        assertThat(response.title()).isEqualTo("My Title");
        assertThat(response.category()).isEqualTo("dev");
        assertThat(response.author()).isNotNull();
        assertThat(response.author().id()).isEqualTo("user-1");
        assertThat(response.author().name()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("toSummaryResponse : doit mapper sans le champ content")
    void shouldMapToSummaryWithoutContent() {
        Article article = Article.builder()
                .id("article-1")
                .slug("my-title")
                .title("My Title")
                .excerpt("My excerpt")
                .content("Should not appear in summary")
                .category(ArticleCategory.GAMING)
                .tags(List.of())
                .published(false)
                .viewCount(0L)
                .readTime(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ArticleSummaryResponse summary = articleMapper.toSummaryResponse(article);

        assertThat(summary.id()).isEqualTo("article-1");
        assertThat(summary.category()).isEqualTo("gaming");
        // ArticleSummaryResponse n'a pas de champ content — compilation garantit cela
    }

    @Test
    @DisplayName("updateEntityFromRequest : ne doit pas écraser les champs ignorés")
    void shouldNotOverwriteIgnoredFields() {
        Article existing = Article.builder()
                .id("article-1")
                .slug("existing-slug")
                .viewCount(42L)
                .readTime(3)
                .build();

        ArticleRequest request = new ArticleRequest(
                "Updated Title", "Updated excerpt", "Updated content",
                ArticleCategory.DESIGN, List.of("css"), null, false
        );

        articleMapper.updateEntityFromRequest(request, existing);

        assertThat(existing.getId()).isEqualTo("article-1");
        assertThat(existing.getSlug()).isEqualTo("existing-slug");
        assertThat(existing.getViewCount()).isEqualTo(42L);
        assertThat(existing.getTitle()).isEqualTo("Updated Title");
        assertThat(existing.getCategory()).isEqualTo(ArticleCategory.DESIGN);
    }

    @Test
    @DisplayName("UserMapper.toAuthor : doit copier les champs User → Author")
    void shouldMapUserToAuthor() {
        User user = User.builder()
                .id("user-1")
                .name("Jane Doe")
                .avatar("https://avatar.png")
                .role(Role.AUTHOR)
                .build();

        Author author = userMapper.toAuthor(user);

        assertThat(author.getId()).isEqualTo("user-1");
        assertThat(author.getName()).isEqualTo("Jane Doe");
        assertThat(author.getAvatar()).isEqualTo("https://avatar.png");
        assertThat(author.getRole()).isEqualTo("AUTHOR");
    }
}

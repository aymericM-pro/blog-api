package com.example.blog.service;

import com.example.blog.domain.Article;
import com.example.blog.domain.Author;
import com.example.blog.domain.User;
import com.example.blog.dto.ArticleDtos.*;
import com.example.blog.enums.ArticleCategory;
import com.example.blog.enums.ArticleError;
import com.example.blog.enums.Role;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.ArticleMapper;
import com.example.blog.mapper.UserMapper;
import com.example.blog.repository.ArticleRepository;
import com.example.blog.repository.AuthorRepository;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ArticleService — Tests unitaires")
class ArticleServiceTest {

    @Mock ArticleRepository articleRepository;
    @Mock UserRepository userRepository;
    @Mock ArticleMapper articleMapper;
    @Mock UserMapper userMapper;
    @Mock MongoTemplate mongoTemplate;
    @Mock AuthorRepository authorRepository;
    @Mock ApplicationEventPublisher eventPublisher;

    @InjectMocks ArticleServiceImpl articleService;

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private User authorUser;
    private User adminUser;
    private Article article;
    private ArticleRequest articleRequest;

    @BeforeEach
    void setUp() {
        authorUser = User.builder()
                .id("user-1")
                .email("author@example.com")
                .name("John Doe")
                .role(Role.AUTHOR)
                .bookmarkedArticleIds(new ArrayList<>())
                .build();

        adminUser = User.builder()
                .id("admin-1")
                .email("admin@example.com")
                .name("Admin")
                .role(Role.ADMIN)
                .bookmarkedArticleIds(new ArrayList<>())
                .build();

        article = Article.builder()
                .id("article-1")
                .slug("test-article")
                .title("Test Article")
                .excerpt("Test excerpt")
                .content("Test content with some words")
                .category(ArticleCategory.DEV)
                .tags(List.of("java", "spring"))
                .authorId("user-1")
                .published(true)
                .viewCount(10L)
                .readTime(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        articleRequest = new ArticleRequest(
                "New Article Title",
                "New excerpt",
                "New content for the article",
                ArticleCategory.DEV,
                List.of("java"),
                null,
                true
        );
    }

    // ─── getArticleById ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("getArticleById")
    class GetArticleById {

        @Test
        @DisplayName("doit retourner l'article et incrémenter viewCount")
        void shouldReturnArticleAndIncrementViewCount() {
            when(articleRepository.findById("article-1")).thenReturn(Optional.of(article));
            when(articleRepository.save(any())).thenReturn(article);

            AuthorResponse authorResponse = new AuthorResponse("user-1", "John Doe", null, "AUTHOR");
            ArticleResponse response = new ArticleResponse(
                    "article-1", "test-article", "Test Article", "Test excerpt",
                    "Test content", "dev", List.of("java"), authorResponse,
                    null, 1, 11L, null, true, false,
                    LocalDateTime.now(), LocalDateTime.now()
            );
            when(articleMapper.toResponse(any())).thenReturn(response);

            ArticleResponse result = articleService.getArticleById("article-1", null);

            assertThat(result).isNotNull();
            verify(articleRepository).save(argThat(a -> a.getViewCount() == 11L));
        }

        @Test
        @DisplayName("doit lever ARTICLE_NOT_FOUND si l'article n'existe pas")
        void shouldThrowWhenNotFound() {
            when(articleRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> articleService.getArticleById("unknown", null))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(ArticleError.ARTICLE_NOT_FOUND));
        }

        @Test
        @DisplayName("doit marquer isBookmarked = true si l'utilisateur a bookmarké l'article")
        void shouldMarkBookmarkedForCurrentUser() {
            authorUser.setBookmarkedArticleIds(new ArrayList<>(List.of("article-1")));
            when(articleRepository.findById("article-1")).thenReturn(Optional.of(article));
            when(articleRepository.save(any())).thenReturn(article);
            when(userRepository.findByEmail("author@example.com")).thenReturn(Optional.of(authorUser));

            AuthorResponse authorResponse = new AuthorResponse("user-1", "John Doe", null, "AUTHOR");
            ArticleResponse baseResponse = new ArticleResponse(
                    "article-1", "test-article", "Test Article", "Test excerpt",
                    "Test content", "dev", List.of("java"), authorResponse,
                    null, 1, 10L, null, true, false,
                    LocalDateTime.now(), LocalDateTime.now()
            );
            when(articleMapper.toResponse(any())).thenReturn(baseResponse);

            ArticleResponse result = articleService.getArticleById("article-1", "author@example.com");

            assertThat(result.isBookmarked()).isTrue();
        }
    }

    // ─── createArticle ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("createArticle")
    class CreateArticle {

        @Test
        @DisplayName("doit créer un article avec slug généré et readTime calculé")
        void shouldCreateArticleWithSlugAndReadTime() {
            when(articleRepository.existsByTitle(anyString())).thenReturn(false);
            when(userRepository.findByEmail("author@example.com")).thenReturn(Optional.of(authorUser));
            when(authorRepository.findByUserId("user-1")).thenReturn(Optional.empty());
            when(authorRepository.save(any())).thenReturn(
                    Author.builder().id("author-1").userId("user-1").name("John Doe").role("AUTHOR").build()
            );
            when(articleRepository.existsBySlug(anyString())).thenReturn(false);
            when(articleMapper.toEntity(any())).thenReturn(article);
            when(userMapper.toAuthor(any())).thenReturn(Author.builder().id("user-1").build());
            when(articleRepository.save(any())).thenReturn(article);

            AuthorResponse authorResponse = new AuthorResponse("user-1", "John Doe", null, "AUTHOR");
            ArticleResponse response = new ArticleResponse(
                    "article-1", "new-article-title", "New Article Title", "New excerpt",
                    "New content", "dev", List.of("java"), authorResponse,
                    LocalDateTime.now(), 1, 0L, null, true, false,
                    LocalDateTime.now(), LocalDateTime.now()
            );
            when(articleMapper.toResponse(any())).thenReturn(response);

            ArticleResponse result = articleService.createArticle(articleRequest, "author@example.com");

            assertThat(result).isNotNull();
            verify(articleRepository).save(any(Article.class));
        }

        @Test
        @DisplayName("doit lever ARTICLE_TITLE_ALREADY_EXISTS si le titre existe déjà")
        void shouldThrowWhenTitleExists() {
            when(articleRepository.existsByTitle("New Article Title")).thenReturn(true);

            assertThatThrownBy(() -> articleService.createArticle(articleRequest, "author@example.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(ArticleError.ARTICLE_TITLE_ALREADY_EXISTS));
        }

        @Test
        @DisplayName("doit générer un slug avec suffixe en cas de collision")
        void shouldGenerateSlugWithSuffixOnCollision() {
            when(articleRepository.existsByTitle(anyString())).thenReturn(false);
            when(userRepository.findByEmail("author@example.com")).thenReturn(Optional.of(authorUser));
            when(authorRepository.findByUserId("user-1")).thenReturn(Optional.empty());
            when(authorRepository.save(any())).thenReturn(
                    Author.builder().id("author-1").userId("user-1").name("John Doe").role("AUTHOR").build()
            );
            when(articleRepository.existsBySlug("new-article-title")).thenReturn(true);
            when(articleRepository.existsBySlug("new-article-title-2")).thenReturn(false);
            when(articleMapper.toEntity(any())).thenReturn(article);
            when(userMapper.toAuthor(any())).thenReturn(Author.builder().id("user-1").build());
            when(articleRepository.save(any())).thenReturn(article);
            when(articleMapper.toResponse(any())).thenReturn(mock(ArticleResponse.class));

            articleService.createArticle(articleRequest, "author@example.com");

            verify(articleRepository).save(argThat(a -> "new-article-title-2".equals(a.getSlug())));
        }
    }

    // ─── deleteArticle ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("deleteArticle")
    class DeleteArticle {

        @Test
        @DisplayName("l'auteur peut supprimer son propre article")
        void authorCanDeleteOwnArticle() {
            when(articleRepository.findById("article-1")).thenReturn(Optional.of(article));

            articleService.deleteArticle("article-1", "author@example.com");

            verify(articleRepository).delete(article);
        }

        @Test
        @DisplayName("n'importe quel utilisateur authentifié peut supprimer un article")
        void anyAuthenticatedUserCanDeleteArticle() {
            when(articleRepository.findById("article-1")).thenReturn(Optional.of(article));

            articleService.deleteArticle("article-1", "other@example.com");

            verify(articleRepository).delete(article);
        }
    }

    // ─── toggleBookmark ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("toggleBookmark")
    class ToggleBookmark {

        @Test
        @DisplayName("doit ajouter un bookmark s'il n'existe pas")
        void shouldAddBookmark() {
            when(articleRepository.findById("article-1")).thenReturn(Optional.of(article));
            when(userRepository.findByEmail("author@example.com")).thenReturn(Optional.of(authorUser));
            when(userRepository.save(any())).thenReturn(authorUser);
            when(articleMapper.toResponse(any())).thenReturn(mock(ArticleResponse.class));

            articleService.toggleBookmark("article-1", "author@example.com");

            verify(userRepository).save(argThat(u -> u.getBookmarkedArticleIds().contains("article-1")));
        }

        @Test
        @DisplayName("doit retirer le bookmark s'il existe déjà")
        void shouldRemoveBookmark() {
            authorUser.setBookmarkedArticleIds(new ArrayList<>(List.of("article-1")));
            when(articleRepository.findById("article-1")).thenReturn(Optional.of(article));
            when(userRepository.findByEmail("author@example.com")).thenReturn(Optional.of(authorUser));
            when(userRepository.save(any())).thenReturn(authorUser);
            when(articleMapper.toResponse(any())).thenReturn(mock(ArticleResponse.class));

            articleService.toggleBookmark("article-1", "author@example.com");

            verify(userRepository).save(argThat(u -> !u.getBookmarkedArticleIds().contains("article-1")));
        }


    }
}

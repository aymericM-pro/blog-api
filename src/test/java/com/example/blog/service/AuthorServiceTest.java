package com.example.blog.service;

import com.example.blog.domain.Author;
import com.example.blog.dto.AuthorsDtos.AuthorRequest;
import com.example.blog.dto.AuthorsDtos.AuthorResponse;
import com.example.blog.enums.AuthorError;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.AuthorMapper;
import com.example.blog.repository.AuthorRepository;
import com.example.blog.service.impl.AuthorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService — Tests unitaires")
class AuthorServiceTest {

    @Mock AuthorRepository authorRepository;
    @Mock AuthorMapper mapper;

    @InjectMocks AuthorServiceImpl authorService;

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private Author author;
    private AuthorRequest updateRequest;

    @BeforeEach
    void setUp() {
        author = Author.builder()
                .id("author-1")
                .userId("user-1")
                .name("John Doe")
                .avatar("https://avatar.url")
                .role("AUTHOR")
                .description("Old description")
                .build();

        updateRequest = new AuthorRequest("John Updated", "https://new-avatar.url", "AUTHOR", "New description");
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("tout utilisateur authentifié peut mettre à jour un profil auteur")
        void anyAuthenticatedUserCanUpdate() {
            when(authorRepository.findById("author-1")).thenReturn(Optional.of(author));
            when(authorRepository.save(any())).thenReturn(author);
            when(mapper.toResponse(any())).thenReturn(
                    new AuthorResponse("author-1", "John Updated", "https://new-avatar.url", "AUTHOR", "New description", null)
            );

            AuthorResponse result = authorService.update("author-1", updateRequest, "any@example.com");

            assertThat(result).isNotNull();
            assertThat(result.description()).isEqualTo("New description");
            verify(mapper).updateAuthorFromDto(updateRequest, author);
            verify(authorRepository).save(author);
        }

        @Test
        @DisplayName("doit lever AUTHOR_NOT_FOUND si l'auteur n'existe pas")
        void shouldThrowWhenAuthorNotFound() {
            when(authorRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authorService.update("unknown", updateRequest, "any@example.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(AuthorError.AUTHOR_NOT_FOUND));
        }
    }
}

package com.example.blog.service;

import com.example.blog.domain.Author;
import com.example.blog.domain.User;
import com.example.blog.dto.AuthorsDtos.AuthorRequest;
import com.example.blog.dto.AuthorsDtos.AuthorResponse;
import com.example.blog.enums.AuthorError;
import com.example.blog.enums.Role;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.AuthorMapper;
import com.example.blog.repository.AuthorRepository;
import com.example.blog.repository.UserRepository;
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
    @Mock UserRepository userRepository;
    @Mock AuthorMapper mapper;

    @InjectMocks AuthorServiceImpl authorService;

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private User ownerUser;
    private User adminUser;
    private User otherUser;
    private Author author;
    private AuthorRequest updateRequest;

    @BeforeEach
    void setUp() {
        ownerUser = User.builder()
                .id("user-1")
                .email("owner@example.com")
                .role(Role.AUTHOR)
                .build();

        adminUser = User.builder()
                .id("admin-1")
                .email("admin@example.com")
                .role(Role.ADMIN)
                .build();

        otherUser = User.builder()
                .id("other-99")
                .email("other@example.com")
                .role(Role.USER)
                .build();

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
        @DisplayName("le propriétaire peut mettre à jour son profil")
        void ownerCanUpdateOwnProfile() {
            when(authorRepository.findById("author-1")).thenReturn(Optional.of(author));
            when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(ownerUser));
            when(authorRepository.save(any())).thenReturn(author);
            when(mapper.toResponse(any())).thenReturn(
                    new AuthorResponse("author-1", "John Updated", "https://new-avatar.url", "AUTHOR", "New description", null)
            );

            AuthorResponse result = authorService.update("author-1", updateRequest, "owner@example.com");

            assertThat(result).isNotNull();
            assertThat(result.description()).isEqualTo("New description");
            verify(mapper).updateAuthorFromDto(updateRequest, author);
            verify(authorRepository).save(author);
        }

        @Test
        @DisplayName("l'admin peut mettre à jour n'importe quel profil auteur")
        void adminCanUpdateAnyProfile() {
            when(authorRepository.findById("author-1")).thenReturn(Optional.of(author));
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(authorRepository.save(any())).thenReturn(author);
            when(mapper.toResponse(any())).thenReturn(
                    new AuthorResponse("author-1", "John Updated", null, "AUTHOR", "New description", null)
            );

            AuthorResponse result = authorService.update("author-1", updateRequest, "admin@example.com");

            assertThat(result).isNotNull();
            verify(authorRepository).save(author);
        }

        @Test
        @DisplayName("un autre utilisateur ne peut pas modifier le profil d'un auteur → AUTHOR_FORBIDDEN")
        void otherUserCannotUpdateProfile() {
            when(authorRepository.findById("author-1")).thenReturn(Optional.of(author));
            when(userRepository.findByEmail("other@example.com")).thenReturn(Optional.of(otherUser));

            assertThatThrownBy(() -> authorService.update("author-1", updateRequest, "other@example.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(AuthorError.AUTHOR_FORBIDDEN));

            verify(authorRepository, never()).save(any());
        }

        @Test
        @DisplayName("doit lever AUTHOR_NOT_FOUND si l'auteur n'existe pas")
        void shouldThrowWhenAuthorNotFound() {
            when(authorRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authorService.update("unknown", updateRequest, "owner@example.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(AuthorError.AUTHOR_NOT_FOUND));
        }
    }
}

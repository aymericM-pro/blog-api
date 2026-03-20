package com.example.blog.service;

import com.example.blog.domain.User;
import com.example.blog.dto.UsersDtos.UserResponse;
import com.example.blog.dto.UsersDtos.UserUpdateRequest;
import com.example.blog.enums.UserError;
import com.example.blog.exception.BusinessException;
import com.example.blog.mapper.UserMapper;
import com.example.blog.repository.UserRepository;
import com.example.blog.service.impl.UserServiceImpl;
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
@DisplayName("UserService — Tests unitaires")
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper mapper;

    @InjectMocks UserServiceImpl userService;

    // ─── Fixtures ─────────────────────────────────────────────────────────────

    private User targetUser;
    private User adminUser;
    private User otherUser;
    private UserUpdateRequest updateRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        targetUser = User.builder()
                .id("user-1")
                .email("user@example.com")
                .name("John Doe")
                .role(Role.USER)
                .build();

        adminUser = User.builder()
                .id("admin-1")
                .email("admin@example.com")
                .name("Admin")
                .role(Role.ADMIN)
                .build();

        otherUser = User.builder()
                .id("other-99")
                .email("other@example.com")
                .name("Other")
                .role(Role.USER)
                .build();

        updateRequest = new UserUpdateRequest("John Updated", "https://new-avatar.url", null);
        userResponse = new UserResponse("user-1", "user@example.com", "John Updated", "https://new-avatar.url", "USER", null);
    }

    // ─── update ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("tout utilisateur authentifié peut modifier un profil")
        void anyAuthenticatedUserCanUpdate() {
            when(userRepository.findById("user-1")).thenReturn(Optional.of(targetUser));
            when(userRepository.save(any())).thenReturn(targetUser);
            when(mapper.toResponse(any())).thenReturn(userResponse);

            UserResponse result = userService.update("user-1", updateRequest, "user@example.com");

            assertThat(result).isNotNull();
            verify(mapper).updateUserFromDto(updateRequest, targetUser);
            verify(userRepository).save(targetUser);
        }

        @Test
        @DisplayName("doit lever USER_NOT_FOUND si l'utilisateur n'existe pas")
        void shouldThrowWhenUserNotFound() {
            when(userRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.update("unknown", updateRequest, "user@example.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(UserError.USER_NOT_FOUND));
        }
    }

    // ─── delete ───────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("l'admin peut supprimer un utilisateur")
        void adminCanDeleteUser() {
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(userRepository.findById("user-1")).thenReturn(Optional.of(targetUser));

            userService.delete("user-1", "admin@example.com");

            verify(userRepository).delete(targetUser);
        }

        @Test
        @DisplayName("un non-admin ne peut pas supprimer → USER_FORBIDDEN")
        void nonAdminCannotDelete() {
            when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(targetUser));

            assertThatThrownBy(() -> userService.delete("user-1", "user@example.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(UserError.USER_FORBIDDEN));

            verify(userRepository, never()).delete(any());
        }

        @Test
        @DisplayName("doit lever USER_NOT_FOUND si l'utilisateur cible n'existe pas")
        void shouldThrowWhenTargetNotFound() {
            when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
            when(userRepository.findById("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.delete("unknown", "admin@example.com"))
                    .isInstanceOf(BusinessException.class)
                    .satisfies(ex -> assertThat(((BusinessException) ex).getError())
                            .isEqualTo(UserError.USER_NOT_FOUND));
        }
    }
}

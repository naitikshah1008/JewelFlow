package com.jewelflow.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserManagementServiceTest {

    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final UserInviteRepository userInviteRepository = mock(UserInviteRepository.class);
    private final PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
    private final UserManagementService userManagementService = new UserManagementService(
            appUserRepository,
            userInviteRepository,
            passwordEncoder
    );

    @Test
    void createUserRejectsDuplicateUsername() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(" admin ");
        request.setPassword("AdminDemo123!");
        request.setRole(UserRole.ADMIN);

        when(appUserRepository.existsByUsernameIgnoreCase("admin")).thenReturn(true);

        assertThatThrownBy(() -> userManagementService.createUser(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User already exists");
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createUserHashesPasswordAndEnablesAccount() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(" manager ");
        request.setPassword("Manager123!");
        request.setRole(UserRole.STAFF);
        AppUser savedUser = AppUser.builder()
                .id(2L)
                .username("manager")
                .passwordHash("hashed-password")
                .role(UserRole.STAFF)
                .enabled(true)
                .build();

        when(appUserRepository.existsByUsernameIgnoreCase("manager")).thenReturn(false);
        when(passwordEncoder.encode("Manager123!")).thenReturn("hashed-password");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser);

        UserResponse response = userManagementService.createUser(request);

        assertThat(response.getUsername()).isEqualTo("manager");
        assertThat(response.isEnabled()).isTrue();
        verify(appUserRepository).save(org.mockito.ArgumentMatchers.argThat(user ->
                user.getPasswordHash().equals("hashed-password") && user.isEnabled()
        ));
    }

    @Test
    void updateUserRejectsDisablingLastEnabledAdmin() {
        AppUser admin = AppUser.builder()
                .id(1L)
                .username("admin")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole(UserRole.ADMIN);
        request.setEnabled(false);

        when(appUserRepository.findById(1L)).thenReturn(Optional.of(admin));
        when(appUserRepository.countByRoleAndEnabledTrue(UserRole.ADMIN)).thenReturn(1L);

        assertThatThrownBy(() -> userManagementService.updateUser(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("At least one enabled admin");
        verify(appUserRepository, never()).save(any(AppUser.class));
    }

    @Test
    void createInviteRejectsExistingUsername() {
        CreateUserInviteRequest request = new CreateUserInviteRequest();
        request.setUsername(" staff ");
        request.setRole(UserRole.STAFF);
        when(appUserRepository.existsByUsernameIgnoreCase("staff")).thenReturn(true);

        assertThatThrownBy(() -> userManagementService.createInvite(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User already exists");
        verify(userInviteRepository, never()).save(any(UserInvite.class));
    }

    @Test
    void createInviteReturnsRawTokenAndStoresHash() {
        CreateUserInviteRequest request = new CreateUserInviteRequest();
        request.setUsername(" associate ");
        request.setRole(UserRole.STAFF);
        when(appUserRepository.existsByUsernameIgnoreCase("associate")).thenReturn(false);
        when(userInviteRepository.findTopByUsernameIgnoreCaseAndAcceptedAtIsNullOrderByCreatedAtDesc("associate"))
                .thenReturn(Optional.empty());
        when(userInviteRepository.save(any(UserInvite.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserInviteResponse response = userManagementService.createInvite(request);

        assertThat(response.getUsername()).isEqualTo("associate");
        assertThat(response.getToken()).isNotBlank();
        assertThat(response.getExpiresAt()).isNotNull();
        verify(userInviteRepository).save(org.mockito.ArgumentMatchers.argThat(invite ->
                invite.getTokenHash() != null
                        && !invite.getTokenHash().equals(response.getToken())
                        && invite.getRole() == UserRole.STAFF
        ));
    }

    @Test
    void acceptInviteCreatesEnabledUserAndMarksInviteAccepted() {
        AcceptUserInviteRequest request = new AcceptUserInviteRequest();
        request.setToken("raw-token");
        request.setPassword("Welcome123!");
        UserInvite invite = UserInvite.builder()
                .id(1L)
                .username("associate")
                .tokenHash(userManagementService.hashToken("raw-token"))
                .role(UserRole.STAFF)
                .expiresAt(java.time.LocalDateTime.now().plusDays(1))
                .build();
        AppUser savedUser = AppUser.builder()
                .id(2L)
                .username("associate")
                .passwordHash("hashed-password")
                .role(UserRole.STAFF)
                .enabled(true)
                .build();

        when(userInviteRepository.findByTokenHashAndAcceptedAtIsNull(userManagementService.hashToken("raw-token")))
                .thenReturn(Optional.of(invite));
        when(appUserRepository.existsByUsernameIgnoreCase("associate")).thenReturn(false);
        when(passwordEncoder.encode("Welcome123!")).thenReturn("hashed-password");
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUser);

        UserResponse response = userManagementService.acceptInvite(request);

        assertThat(response.getUsername()).isEqualTo("associate");
        assertThat(response.isEnabled()).isTrue();
        assertThat(invite.getAcceptedAt()).isNotNull();
        verify(userInviteRepository).save(invite);
    }
}

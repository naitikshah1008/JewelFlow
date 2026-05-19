package com.jewelflow.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RefreshTokenServiceTest {

    private final RefreshTokenRepository refreshTokenRepository = mock(RefreshTokenRepository.class);
    private final RefreshTokenService refreshTokenService = new RefreshTokenService(refreshTokenRepository);

    @Test
    void issueTokenStoresOnlyHashedToken() {
        ReflectionTestUtils.setField(refreshTokenService, "expirationDays", 14L);
        AppUser user = AppUser.builder()
                .id(1L)
                .username("admin")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        IssuedRefreshToken issued = refreshTokenService.issueToken(user);

        assertThat(issued.token()).isNotBlank();
        assertThat(issued.expiresAt()).isAfter(LocalDateTime.now().plusDays(13));
        verify(refreshTokenRepository).save(org.mockito.ArgumentMatchers.argThat(saved ->
                saved.getUser().equals(user)
                        && !saved.getTokenHash().equals(issued.token())
                        && !saved.isRevoked()
        ));
    }

    @Test
    void rotateRevokesCurrentTokenAndIssuesReplacement() {
        ReflectionTestUtils.setField(refreshTokenService, "expirationDays", 14L);
        AppUser user = AppUser.builder()
                .id(1L)
                .username("admin")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
        String rawToken = "refresh-token";
        RefreshToken storedToken = RefreshToken.builder()
                .user(user)
                .tokenHash(refreshTokenService.hashToken(rawToken))
                .expiresAt(LocalDateTime.now().plusDays(1))
                .revoked(false)
                .build();

        when(refreshTokenRepository.findByTokenHashAndRevokedFalse(refreshTokenService.hashToken(rawToken)))
                .thenReturn(Optional.of(storedToken));
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenExchange exchange = refreshTokenService.rotate(rawToken);

        assertThat(storedToken.isRevoked()).isTrue();
        assertThat(storedToken.getRevokedAt()).isNotNull();
        assertThat(exchange.user()).isEqualTo(user);
        assertThat(exchange.refreshToken().token()).isNotEqualTo(rawToken);
    }
}

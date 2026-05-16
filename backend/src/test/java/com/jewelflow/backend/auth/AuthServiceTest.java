package com.jewelflow.backend.auth;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final AppUserRepository appUserRepository = mock(AppUserRepository.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthService authService = new AuthService(authenticationManager, appUserRepository, jwtService);

    @Test
    void loginAuthenticatesAndReturnsJwtResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername(" admin ");
        request.setPassword("secret");
        AppUser user = AppUser.builder()
                .username("admin")
                .role(UserRole.ADMIN)
                .enabled(true)
                .build();
        AuthResponse expected = AuthResponse.builder()
                .token("jwt-token")
                .tokenType("Bearer")
                .expiresAt(Instant.parse("2026-05-16T20:00:00Z"))
                .username("admin")
                .role(UserRole.ADMIN)
                .build();

        when(appUserRepository.findByUsernameIgnoreCase("admin")).thenReturn(Optional.of(user));
        when(jwtService.issueToken(user)).thenReturn(expected);

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        assertThat(response.getToken()).isEqualTo("jwt-token");
        assertThat(response.getRole()).isEqualTo(UserRole.ADMIN);
    }
}

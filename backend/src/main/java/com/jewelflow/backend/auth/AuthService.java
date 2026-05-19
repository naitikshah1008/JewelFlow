package com.jewelflow.backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse login(LoginRequest request) {
        String username = request.getUsername().trim();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.getPassword())
        );
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return issueSession(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        RefreshTokenExchange exchange = refreshTokenService.rotate(request.getRefreshToken());
        return buildResponse(exchange.user(), exchange.refreshToken());
    }

    public void logout(LogoutRequest request) {
        refreshTokenService.revoke(request.getRefreshToken());
    }

    public CurrentUserResponse getCurrentUser(String username) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        return CurrentUserResponse.builder()
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }

    private AuthResponse issueSession(AppUser user) {
        IssuedRefreshToken refreshToken = refreshTokenService.issueToken(user);
        return buildResponse(user, refreshToken);
    }

    private AuthResponse buildResponse(AppUser user, IssuedRefreshToken refreshToken) {
        AuthResponse accessToken = jwtService.issueToken(user);
        return AuthResponse.builder()
                .token(accessToken.getToken())
                .tokenType(accessToken.getTokenType())
                .expiresAt(accessToken.getExpiresAt())
                .refreshToken(refreshToken.token())
                .refreshExpiresAt(refreshToken.expiresAt())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}

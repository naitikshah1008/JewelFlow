package com.jewelflow.backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jewelflow.security.refresh-token.expiration-days}")
    private long expirationDays;

    @Transactional
    public IssuedRefreshToken issueToken(AppUser user) {
        String token = UUID.randomUUID() + "." + UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expirationDays);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(token))
                .expiresAt(expiresAt)
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return new IssuedRefreshToken(token, expiresAt);
    }

    @Transactional
    public RefreshTokenExchange rotate(String token) {
        RefreshToken currentToken = refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(token))
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));
        if (currentToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            revokeToken(currentToken);
            throw new IllegalArgumentException("Refresh token expired");
        }
        AppUser user = currentToken.getUser();
        if (!user.isEnabled()) {
            revokeToken(currentToken);
            throw new IllegalArgumentException("User account is disabled");
        }
        revokeToken(currentToken);
        return new RefreshTokenExchange(user, issueToken(user));
    }

    @Transactional
    public void revoke(String token) {
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(token))
                .ifPresent(this::revokeToken);
    }

    private void revokeToken(RefreshToken token) {
        token.setRevoked(true);
        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);
    }

    String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 hashing is unavailable", exception);
        }
    }
}

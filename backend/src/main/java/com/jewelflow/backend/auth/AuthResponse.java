package com.jewelflow.backend.auth;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private Instant expiresAt;
    private String refreshToken;
    private LocalDateTime refreshExpiresAt;
    private String username;
    private UserRole role;
}

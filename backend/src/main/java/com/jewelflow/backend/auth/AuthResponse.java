package com.jewelflow.backend.auth;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private Instant expiresAt;
    private String username;
    private UserRole role;
}

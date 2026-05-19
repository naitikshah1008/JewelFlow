package com.jewelflow.backend.auth;

import java.time.LocalDateTime;

public record IssuedRefreshToken(String token, LocalDateTime expiresAt) {
}

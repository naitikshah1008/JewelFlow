package com.jewelflow.backend.auth;

public record RefreshTokenExchange(AppUser user, IssuedRefreshToken refreshToken) {
}

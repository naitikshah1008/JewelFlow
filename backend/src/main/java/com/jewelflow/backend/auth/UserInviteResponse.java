package com.jewelflow.backend.auth;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserInviteResponse {

    private String username;
    private UserRole role;
    private String token;
    private LocalDateTime expiresAt;
}

package com.jewelflow.backend.auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUserResponse {

    private String username;
    private UserRole role;
}

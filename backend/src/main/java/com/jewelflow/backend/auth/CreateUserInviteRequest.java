package com.jewelflow.backend.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateUserInviteRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotNull(message = "Role is required")
    private UserRole role;
}

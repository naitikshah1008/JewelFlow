package com.jewelflow.backend.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUserRequest {

    @NotNull(message = "Role is required")
    private UserRole role;

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;
}

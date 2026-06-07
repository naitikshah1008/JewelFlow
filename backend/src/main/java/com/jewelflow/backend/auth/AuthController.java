package com.jewelflow.backend.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserManagementService userManagementService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public void logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
    }

    @PostMapping("/invites/accept")
    public UserResponse acceptInvite(@Valid @RequestBody AcceptUserInviteRequest request) {
        return userManagementService.acceptInvite(request);
    }

    @GetMapping("/me")
    public CurrentUserResponse currentUser(Authentication authentication) {
        return authService.getCurrentUser(authentication.getName());
    }
}

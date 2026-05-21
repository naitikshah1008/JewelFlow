package com.jewelflow.backend.auth;

import com.jewelflow.backend.common.PageRequestFactory;
import com.jewelflow.backend.common.PageResponse;
import com.jewelflow.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private static final Map<String, String> ALLOWED_SORTS = Map.of(
            "username", "username",
            "role", "role",
            "enabled", "enabled",
            "createdAt", "createdAt",
            "updatedAt", "updatedAt"
    );

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getUsers() {
        return appUserRepository.findAllByOrderByUsernameAsc()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    public PageResponse<UserResponse> getUsersPage(Integer page, Integer size, String sortBy, String direction) {
        Page<AppUser> users = appUserRepository.findAll(
                PageRequestFactory.create(page, size, sortBy, direction, ALLOWED_SORTS, "username")
        );
        return PageResponse.from(users, UserResponse::from);
    }

    public UserResponse createUser(CreateUserRequest request) {
        String username = normalizeUsername(request.getUsername());
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("User already exists with username: " + username);
        }
        AppUser user = AppUser.builder()
                .username(username)
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .enabled(true)
                .build();
        return UserResponse.from(appUserRepository.save(user));
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        AppUser user = getUser(id);
        ensureAtLeastOneEnabledAdminRemains(user, request.getRole(), request.getEnabled());
        user.setRole(request.getRole());
        user.setEnabled(request.getEnabled());
        return UserResponse.from(appUserRepository.save(user));
    }

    public UserResponse resetPassword(Long id, ResetPasswordRequest request) {
        AppUser user = getUser(id);
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return UserResponse.from(appUserRepository.save(user));
    }

    private AppUser getUser(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    private void ensureAtLeastOneEnabledAdminRemains(AppUser user, UserRole requestedRole, Boolean requestedEnabled) {
        boolean currentlyEnabledAdmin = user.isEnabled() && user.getRole() == UserRole.ADMIN;
        boolean willRemainEnabledAdmin = Boolean.TRUE.equals(requestedEnabled) && requestedRole == UserRole.ADMIN;
        if (currentlyEnabledAdmin && !willRemainEnabledAdmin
                && appUserRepository.countByRoleAndEnabledTrue(UserRole.ADMIN) <= 1) {
            throw new IllegalArgumentException("At least one enabled admin user is required");
        }
    }

    private String normalizeUsername(String username) {
        return username == null ? "" : username.trim();
    }
}

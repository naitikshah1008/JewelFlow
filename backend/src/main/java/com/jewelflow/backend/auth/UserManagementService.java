package com.jewelflow.backend.auth;

import com.jewelflow.backend.common.PageRequestFactory;
import com.jewelflow.backend.common.PageResponse;
import com.jewelflow.backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private final UserInviteRepository userInviteRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jewelflow.security.user-invite.expiration-days:7}")
    private long inviteExpirationDays = 7;

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

    @Transactional
    public UserInviteResponse createInvite(CreateUserInviteRequest request) {
        String username = normalizeUsername(request.getUsername());
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalArgumentException("User already exists with username: " + username);
        }
        userInviteRepository.findTopByUsernameIgnoreCaseAndAcceptedAtIsNullOrderByCreatedAtDesc(username)
                .filter(invite -> invite.getExpiresAt().isAfter(LocalDateTime.now()))
                .ifPresent(invite -> {
                    throw new IllegalArgumentException("Pending invite already exists for username: " + username);
                });

        String token = UUID.randomUUID() + "." + UUID.randomUUID();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(inviteExpirationDays);
        UserInvite invite = UserInvite.builder()
                .username(username)
                .role(request.getRole())
                .tokenHash(hashToken(token))
                .expiresAt(expiresAt)
                .build();
        userInviteRepository.save(invite);
        return UserInviteResponse.builder()
                .username(username)
                .role(request.getRole())
                .token(token)
                .expiresAt(expiresAt)
                .build();
    }

    @Transactional
    public UserResponse acceptInvite(AcceptUserInviteRequest request) {
        UserInvite invite = userInviteRepository.findByTokenHashAndAcceptedAtIsNull(hashToken(request.getToken()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid invite token"));
        if (invite.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invite token expired");
        }
        if (appUserRepository.existsByUsernameIgnoreCase(invite.getUsername())) {
            throw new IllegalArgumentException("User already exists with username: " + invite.getUsername());
        }

        AppUser user = AppUser.builder()
                .username(invite.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(invite.getRole())
                .enabled(true)
                .build();
        AppUser savedUser = appUserRepository.save(user);
        invite.setAcceptedAt(LocalDateTime.now());
        userInviteRepository.save(invite);
        return UserResponse.from(savedUser);
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

    String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashed);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 hashing is unavailable", exception);
        }
    }
}

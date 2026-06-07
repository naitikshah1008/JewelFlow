package com.jewelflow.backend.auth;

import com.jewelflow.backend.common.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserManagementService userManagementService;

    @GetMapping
    public List<UserResponse> getUsers() {
        return userManagementService.getUsers();
    }

    @GetMapping("/page")
    public PageResponse<UserResponse> getUsersPage(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String direction
    ) {
        return userManagementService.getUsersPage(page, size, sortBy, direction);
    }

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return userManagementService.createUser(request);
    }

    @PostMapping("/invites")
    public UserInviteResponse createInvite(@Valid @RequestBody CreateUserInviteRequest request) {
        return userManagementService.createInvite(request);
    }

    @PutMapping("/{id}")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return userManagementService.updateUser(id, request);
    }

    @PostMapping("/{id}/reset-password")
    public UserResponse resetPassword(@PathVariable Long id, @Valid @RequestBody ResetPasswordRequest request) {
        return userManagementService.resetPassword(id, request);
    }
}

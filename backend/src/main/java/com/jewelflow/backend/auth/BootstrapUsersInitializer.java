package com.jewelflow.backend.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapUsersInitializer implements ApplicationRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jewelflow.security.bootstrap-admin.username}")
    private String adminUsername;

    @Value("${jewelflow.security.bootstrap-admin.password}")
    private String adminPassword;

    @Value("${jewelflow.security.bootstrap-staff.username}")
    private String staffUsername;

    @Value("${jewelflow.security.bootstrap-staff.password}")
    private String staffPassword;

    @Override
    public void run(ApplicationArguments args) {
        createUserIfMissing(adminUsername, adminPassword, UserRole.ADMIN);
        createUserIfMissing(staffUsername, staffPassword, UserRole.STAFF);
    }

    private void createUserIfMissing(String username, String password, UserRole role) {
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            return;
        }
        AppUser user = AppUser.builder()
                .username(username.trim())
                .passwordHash(passwordEncoder.encode(password))
                .role(role)
                .enabled(true)
                .build();
        appUserRepository.save(user);
    }
}

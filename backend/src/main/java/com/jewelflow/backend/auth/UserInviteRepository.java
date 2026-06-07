package com.jewelflow.backend.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInviteRepository extends JpaRepository<UserInvite, Long> {

    Optional<UserInvite> findByTokenHashAndAcceptedAtIsNull(String tokenHash);

    Optional<UserInvite> findTopByUsernameIgnoreCaseAndAcceptedAtIsNullOrderByCreatedAtDesc(String username);
}

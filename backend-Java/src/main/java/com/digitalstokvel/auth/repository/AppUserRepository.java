package com.digitalstokvel.auth.repository;

import com.digitalstokvel.auth.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByMsisdn(String msisdn);

    Optional<AppUser> findBySessionTokenAndSessionExpiresAtAfter(String sessionToken, Instant time);
}

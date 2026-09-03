package com.digitalstokvel.auth;

import com.digitalstokvel.auth.entity.AppUser;
import com.digitalstokvel.auth.repository.AppUserRepository;
import com.digitalstokvel.common.exception.ConflictException;
import com.digitalstokvel.common.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class AuthService {
    private final AppUserRepository users;
    private final PasswordEncoder passwordEncoder;
    public AuthService(AppUserRepository users, PasswordEncoder passwordEncoder) { this.users = users; this.passwordEncoder = passwordEncoder; }
    public AuthController.AuthResponse register(String msisdn, String name, String password) {
        if (users.findByMsisdn(msisdn).isPresent()) throw new ConflictException("An account already exists for this phone number");
        return authenticate(users.save(new AppUser(msisdn, name, passwordEncoder.encode(password))));
    }
    public AuthController.AuthResponse login(String msisdn, String password) {
        AppUser user = users.findByMsisdn(msisdn).orElseThrow(() -> new BusinessException("INVALID_CREDENTIALS", "Invalid phone number or password"));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) throw new BusinessException("INVALID_CREDENTIALS", "Invalid phone number or password");
        return authenticate(user);
    }
    public void logout(AppUser user) {
        users.findById(user.getId()).ifPresent(account -> {
            account.clearSession();
            users.save(account);
        });
    }
    private AuthController.AuthResponse authenticate(AppUser user) {
        String token = UUID.randomUUID().toString();
        user.setSession(token, Instant.now().plus(24, ChronoUnit.HOURS));
        return new AuthController.AuthResponse(token, new AuthController.AuthUser(user.getId(), user.getMsisdn(), user.getDisplayName()));
    }
}

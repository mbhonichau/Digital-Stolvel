package com.digitalstokvel.auth;

import com.digitalstokvel.auth.entity.AppUser;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service) { this.service = service; }
    @PostMapping("/register") @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) { return service.register(request.msisdn(), request.name(), request.password()); }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) { return service.login(request.msisdn(), request.password()); }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal AppUser user) {
        service.logout(user);
        return ResponseEntity.noContent().build();
    }
    public record RegisterRequest(@NotBlank String msisdn, @NotBlank String name, @NotBlank @Size(min = 8) String password) { }
    public record LoginRequest(@NotBlank String msisdn, @NotBlank String password) { }
    public record AuthUser(UUID id, String msisdn, String name) { }
    public record AuthResponse(String token, AuthUser user) { }
}

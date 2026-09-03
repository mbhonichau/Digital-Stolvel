package com.digitalstokvel.auth.entity;

import com.digitalstokvel.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "app_users")
public class AppUser extends BaseEntity {
    @Column(nullable = false, unique = true, length = 20) private String msisdn;
    @Column(name = "display_name", nullable = false, length = 150) private String displayName;
    @Column(name = "password_hash", nullable = false, length = 100) private String passwordHash;
    @Column(name = "session_token", length = 100) private String sessionToken;
    @Column(name = "session_expires_at") private Instant sessionExpiresAt;
    public AppUser() { }
    public AppUser(String msisdn, String displayName, String passwordHash) { this.msisdn = msisdn; this.displayName = displayName; this.passwordHash = passwordHash; }
    public String getMsisdn() { return msisdn; }
    public String getDisplayName() { return displayName; }
    public String getPasswordHash() { return passwordHash; }
    public void setSession(String token, Instant expiresAt) { this.sessionToken = token; this.sessionExpiresAt = expiresAt; }
    public void clearSession() { this.sessionToken = null; this.sessionExpiresAt = null; }
}

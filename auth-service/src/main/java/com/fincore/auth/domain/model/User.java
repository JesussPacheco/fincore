package com.fincore.auth.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final boolean active;
    private final LocalDateTime createdAt;

    public enum Role {
        CUSTOMER, ADMIN
    }

    public User(String name, String email, String passwordHash) {
        this.id           = UUID.randomUUID();
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = Role.CUSTOMER;
        this.active       = true;
        this.createdAt    = LocalDateTime.now();
    }

    public User(UUID id, String name, String email,
                String passwordHash, Role role,
                boolean active, LocalDateTime createdAt) {
        this.id           = id;
        this.name         = name;
        this.email        = email;
        this.passwordHash = passwordHash;
        this.role         = role;
        this.active       = active;
        this.createdAt    = createdAt;
    }

    public UUID getId()             { return id; }
    public String getName()         { return name; }
    public String getEmail()        { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole()           { return role; }
    public boolean isActive()       { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
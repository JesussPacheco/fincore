package com.fincore.auth.infrastructure.persistence;

import com.fincore.auth.domain.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class UserJpaEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private User.Role role;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    // Constructor vacío requerido por JPA
    protected UserJpaEntity() {}

    // Convierte dominio → JPA
    public static UserJpaEntity fromDomain(User user) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.id           = user.getId();
        entity.name         = user.getName();
        entity.email        = user.getEmail();
        entity.passwordHash = user.getPasswordHash();
        entity.role         = user.getRole();
        entity.active       = user.isActive();
        entity.createdAt    = user.getCreatedAt();
        entity.updatedAt    = LocalDateTime.now();
        return entity;
    }

    // Convierte JPA → dominio
    public User toDomain() {
        return new User(id, name, email, passwordHash, role, active, createdAt);
    }
}
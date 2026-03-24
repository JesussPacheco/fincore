package com.fincore.auth.infrastructure.web;

import com.fincore.auth.domain.model.User;
import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String email,
        String name
) {
    // Converts domain User to response DTO
    public static RegisterResponse fromDomain(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }
}
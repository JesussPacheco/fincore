package com.fincore.auth.infrastructure.web;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
    public static LoginResponse of(String accessToken, long expiresInMs) {
        return new LoginResponse(accessToken, "Bearer", expiresInMs / 1000);
    }
}
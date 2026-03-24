package com.fincore.auth.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserRegisteredEvent {

    private final UUID eventId;
    private final UUID userId;
    private final String email;
    private final LocalDateTime occurredAt;

    public UserRegisteredEvent(UUID userId, String email) {
        this.eventId    = UUID.randomUUID();
        this.userId     = userId;
        this.email      = email;
        this.occurredAt = LocalDateTime.now();
    }

    public UUID getEventId()            { return eventId; }
    public UUID getUserId()             { return userId; }
    public String getEmail()            { return email; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
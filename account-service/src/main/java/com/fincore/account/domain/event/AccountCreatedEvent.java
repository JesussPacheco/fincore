package com.fincore.account.domain.event;

import java.time.LocalDateTime;
import java.util.UUID;

public class AccountCreatedEvent {

    private final UUID eventId;
    private final UUID accountId;
    private final UUID userId;
    private final String accountNumber;
    private final LocalDateTime occurredAt;

    public AccountCreatedEvent(UUID accountId, UUID userId, String accountNumber) {
        this.eventId       = UUID.randomUUID();
        this.accountId     = accountId;
        this.userId        = userId;
        this.accountNumber = accountNumber;
        this.occurredAt    = LocalDateTime.now();
    }

    public UUID getEventId()            { return eventId; }
    public UUID getAccountId()          { return accountId; }
    public UUID getUserId()             { return userId; }
    public String getAccountNumber()    { return accountNumber; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}
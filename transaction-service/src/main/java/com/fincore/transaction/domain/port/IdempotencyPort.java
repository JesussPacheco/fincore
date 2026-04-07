package com.fincore.transaction.domain.port;

import java.util.Optional;

public interface IdempotencyPort {

    Optional<String> get(String key);

    void save(String key, String value, long ttlHours);
}
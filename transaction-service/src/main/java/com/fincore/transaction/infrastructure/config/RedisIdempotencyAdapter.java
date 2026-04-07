package com.fincore.transaction.infrastructure.config;

import com.fincore.transaction.domain.port.IdempotencyPort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisIdempotencyAdapter implements IdempotencyPort {

    private final RedisTemplate<String, String> redisTemplate;

    public RedisIdempotencyAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(
                redisTemplate.opsForValue().get("idempotency:" + key)
        );
    }

    @Override
    public void save(String key, String value, long ttlHours) {
        redisTemplate.opsForValue().set(
                "idempotency:" + key,
                value,
                Duration.ofHours(ttlHours)
        );
    }
}
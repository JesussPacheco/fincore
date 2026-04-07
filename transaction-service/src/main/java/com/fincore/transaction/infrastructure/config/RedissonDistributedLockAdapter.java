package com.fincore.transaction.infrastructure.config;

import com.fincore.transaction.domain.port.DistributedLockPort;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class RedissonDistributedLockAdapter implements DistributedLockPort {

    private static final Logger log = LoggerFactory.getLogger(RedissonDistributedLockAdapter.class);

    private final RedissonClient redissonClient;

    public RedissonDistributedLockAdapter(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public boolean acquireLock(String lockKey, long waitSeconds, long leaseSeconds) {
        try {
            RLock lock = redissonClient.getLock(lockKey);
            return lock.tryLock(waitSeconds, leaseSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Lock acquisition interrupted for key={}", lockKey);
            return false;
        }
    }

    @Override
    public void releaseLock(String lockKey) {
        Optional.of(redissonClient.getLock(lockKey))
                .filter(RLock::isHeldByCurrentThread)
                .ifPresent(lock -> {
                    lock.unlock();
                    log.info("Lock released key={}", lockKey);
                });
    }
}
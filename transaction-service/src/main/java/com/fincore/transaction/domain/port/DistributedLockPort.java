package com.fincore.transaction.domain.port;

public interface DistributedLockPort {

    boolean acquireLock(String lockKey, long waitSeconds, long leaseSeconds);

    void releaseLock(String lockKey);
}
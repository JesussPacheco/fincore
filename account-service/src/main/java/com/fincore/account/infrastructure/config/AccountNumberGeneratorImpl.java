package com.fincore.account.infrastructure.config;

import com.fincore.account.domain.port.AccountNumberGenerator;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class AccountNumberGeneratorImpl implements AccountNumberGenerator {

    private static final AtomicLong counter = new AtomicLong(1);

    @Override
    public String generate() {
        return String.format("ACC-%011d", counter.getAndIncrement());
    }
}
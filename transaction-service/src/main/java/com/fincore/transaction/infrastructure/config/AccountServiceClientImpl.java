package com.fincore.transaction.infrastructure.config;

import com.fincore.transaction.domain.port.AccountServiceClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class AccountServiceClientImpl implements AccountServiceClient {

    private static final Logger log = LoggerFactory.getLogger(AccountServiceClientImpl.class);

    private final AccountServiceFeignClient feignClient;

    public AccountServiceClientImpl(AccountServiceFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @Override
    @CircuitBreaker(name = "account-service", fallbackMethod = "validateBalanceFallback")
    public void validateBalance(UUID accountId, BigDecimal amount, String currency) {
        feignClient.validateBalance(accountId, Map.of(
                "amount", amount,
                "currency", currency
        ));
    }

    @Override
    @CircuitBreaker(name = "account-service", fallbackMethod = "creditFallback")
    public void credit(UUID accountId, BigDecimal amount, String currency) {
        feignClient.credit(accountId, Map.of(
                "amount", amount,
                "currency", currency
        ));
    }

    private void validateBalanceFallback(UUID accountId, BigDecimal amount,
                                         String currency, Exception ex) {
        log.error("Circuit breaker open for validateBalance accountId={} error={}",
                accountId, ex.getMessage());
        throw new RuntimeException("Account service unavailable");
    }

    private void creditFallback(UUID accountId, BigDecimal amount,
                                String currency, Exception ex) {
        log.error("Circuit breaker open for credit accountId={} error={}",
                accountId, ex.getMessage());
        throw new RuntimeException("Account service unavailable");
    }
}
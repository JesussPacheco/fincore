package com.fincore.transaction.infrastructure.config;

import com.fincore.transaction.domain.port.AccountServiceClient;
import com.fincore.transaction.domain.port.DistributedLockPort;
import com.fincore.transaction.domain.port.IdempotencyPort;
import com.fincore.transaction.domain.port.TransferRepository;
import com.fincore.transaction.domain.usecase.InitiateTransferUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public InitiateTransferUseCase initiateTransferUseCase(
            TransferRepository transferRepository,
            AccountServiceClient accountServiceClient,
            DistributedLockPort distributedLock,
            IdempotencyPort idempotency) {
        return new InitiateTransferUseCase(
                transferRepository, accountServiceClient,
                distributedLock, idempotency
        );
    }
}
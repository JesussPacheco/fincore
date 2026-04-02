package com.fincore.account.infrastructure.config;

import com.fincore.account.domain.port.AccountNumberGenerator;
import com.fincore.account.domain.port.AccountRepository;
import com.fincore.account.domain.usecase.CreateAccountUseCase;
import com.fincore.account.domain.usecase.GetBalanceUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateAccountUseCase createAccountUseCase(
            AccountNumberGenerator accountNumberGenerator) {
        return new CreateAccountUseCase(accountNumberGenerator);
    }

    @Bean
    public GetBalanceUseCase getBalanceUseCase(
            AccountRepository accountRepository) {
        return new GetBalanceUseCase(accountRepository);
    }
}
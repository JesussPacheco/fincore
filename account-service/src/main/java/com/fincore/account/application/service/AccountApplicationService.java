package com.fincore.account.application.service;

import com.fincore.account.domain.event.AccountCreatedEvent;
import com.fincore.account.domain.model.Account;
import com.fincore.account.domain.model.Account.AccountType;
import com.fincore.account.domain.model.Account.Currency;
import com.fincore.account.domain.port.AccountEventPublisher;
import com.fincore.account.domain.port.AccountRepository;
import com.fincore.account.domain.usecase.CreateAccountUseCase;
import com.fincore.account.domain.usecase.GetBalanceUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountApplicationService {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetBalanceUseCase    getBalanceUseCase;
    private final AccountRepository    accountRepository;
    private final AccountEventPublisher eventPublisher;

    public AccountApplicationService(
            CreateAccountUseCase createAccountUseCase,
            GetBalanceUseCase getBalanceUseCase,
            AccountRepository accountRepository,
            AccountEventPublisher eventPublisher) {
        this.createAccountUseCase = createAccountUseCase;
        this.getBalanceUseCase    = getBalanceUseCase;
        this.accountRepository    = accountRepository;
        this.eventPublisher       = eventPublisher;
    }

    @Transactional
    public Account createAccount(UUID userId, AccountType type, Currency currency) {
        Account account = createAccountUseCase.execute(userId, type, currency);
        Account saved   = accountRepository.save(account);
        eventPublisher.publish(
                "fincore.account.created",
                new AccountCreatedEvent(saved.getId(), saved.getUserId(), saved.getAccountNumber())
        );
        return saved;
    }

    @Transactional(readOnly = true)
    public Account getBalance(UUID accountId, UUID requestingUserId) {
        return getBalanceUseCase.execute(accountId, requestingUserId);
    }
}
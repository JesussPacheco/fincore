package com.fincore.account.application.service;

import com.fincore.account.domain.event.AccountCreatedEvent;
import com.fincore.account.domain.exception.AccountNotFoundException;
import com.fincore.account.domain.model.Account;
import com.fincore.account.domain.model.Account.AccountType;
import com.fincore.account.domain.model.Account.Currency;
import com.fincore.account.domain.port.AccountEventPublisher;
import com.fincore.account.domain.port.AccountRepository;
import com.fincore.account.domain.usecase.CreateAccountUseCase;
import com.fincore.account.domain.usecase.GetBalanceUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
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

    @Transactional(readOnly = true)
    public void validateBalance(UUID accountId, BigDecimal amount, String currency) {
        accountRepository.findById(accountId)
                .map(account -> {
                    account.debit(amount);
                    return account;
                })
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
    }

    @Transactional
    public void credit(UUID accountId, BigDecimal amount, String currency) {
        Optional.of(accountRepository.creditBalance(accountId, amount))
                .filter(rows -> rows > 0)
                .orElseThrow(() -> new AccountNotFoundException(accountId.toString()));
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public void debitForTransfer(UUID accountId, UUID transferId,
                                 BigDecimal amount, String currency) {
        Optional.of(accountRepository.debitBalance(accountId, amount))
                .filter(rows -> rows > 0)
                .ifPresentOrElse(
                        rows -> eventPublisher.publish(
                                "fincore.account.debited",
                                Map.of(
                                        "transferId", transferId.toString(),
                                        "accountId",  accountId.toString(),
                                        "amount",     amount.toString(),
                                        "currency",   currency
                                )
                        ),
                        () -> eventPublisher.publish(
                                "fincore.account.debit.failed",
                                Map.of(
                                        "transferId", transferId.toString(),
                                        "reason",     "Insufficient funds or account not found"
                                )
                        )
                );
    }
}
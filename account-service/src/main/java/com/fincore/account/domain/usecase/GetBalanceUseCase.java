package com.fincore.account.domain.usecase;

import com.fincore.account.domain.exception.AccountNotFoundException;
import com.fincore.account.domain.exception.UnauthorizedAccountAccessException;
import com.fincore.account.domain.model.Account;
import com.fincore.account.domain.port.AccountRepository;

import java.util.UUID;

public class GetBalanceUseCase {

    private final AccountRepository accountRepository;

    public GetBalanceUseCase(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account execute(UUID accountId, UUID requestingUserId) {
        return accountRepository.findById(accountId)
                .filter(account -> account.isOwnedBy(requestingUserId))
                .orElseThrow(() -> accountRepository.findById(accountId)
                        .map(a -> (RuntimeException) new UnauthorizedAccountAccessException())
                        .orElse(new AccountNotFoundException(accountId.toString()))
                );
    }
}
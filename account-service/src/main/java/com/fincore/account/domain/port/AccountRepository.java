package com.fincore.account.domain.port;

import com.fincore.account.domain.model.Account;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID id);

    Optional<Account> findByAccountNumber(String accountNumber);

    int debitBalance(UUID accountId, java.math.BigDecimal amount);

    int creditBalance(UUID accountId, BigDecimal amount);

}
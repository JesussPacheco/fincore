package com.fincore.account.domain.port;

import com.fincore.account.domain.model.Account;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository {

    Account save(Account account);

    Optional<Account> findById(UUID id);

    Optional<Account> findByAccountNumber(String accountNumber);
}
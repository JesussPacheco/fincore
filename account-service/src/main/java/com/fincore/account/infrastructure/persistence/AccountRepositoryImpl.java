package com.fincore.account.infrastructure.persistence;

import com.fincore.account.domain.model.Account;
import com.fincore.account.domain.port.AccountRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;

    public AccountRepositoryImpl(AccountJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Account save(Account account) {
        return jpaRepository.saveAndFlush(
                AccountJpaEntity.fromDomain(account)
        ).toDomain();
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(AccountJpaEntity::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(AccountJpaEntity::toDomain);
    }

    @Override
    public int debitBalance(UUID accountId, BigDecimal amount) {
        return jpaRepository.debitBalance(accountId, amount);
    }

    @Override
    public int creditBalance(UUID accountId, BigDecimal amount) {
        return jpaRepository.creditBalance(accountId, amount);
    }
}
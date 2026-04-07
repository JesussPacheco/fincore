package com.fincore.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface AccountJpaRepository extends JpaRepository<AccountJpaEntity, UUID> {

    Optional<AccountJpaEntity> findByAccountNumber(String accountNumber);

    @Modifying
    @Query("UPDATE AccountJpaEntity a SET a.balance = a.balance - :amount, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id AND a.balance >= :amount")
    int debitBalance(@Param("id") UUID id, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE AccountJpaEntity a SET a.balance = a.balance + :amount, a.updatedAt = CURRENT_TIMESTAMP WHERE a.id = :id")
    int creditBalance(@Param("id") UUID id, @Param("amount") BigDecimal amount);

}
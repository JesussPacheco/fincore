package com.fincore.transaction.infrastructure.persistence;

import com.fincore.transaction.domain.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface TransferJpaRepository extends JpaRepository<TransferJpaEntity, UUID> {

    Optional<TransferJpaEntity> findByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query("UPDATE TransferJpaEntity t SET t.status = :status, t.updatedAt = CURRENT_TIMESTAMP WHERE t.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") Transfer.TransferStatus status);
}
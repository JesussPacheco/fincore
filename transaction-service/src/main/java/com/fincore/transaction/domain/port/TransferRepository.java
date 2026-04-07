package com.fincore.transaction.domain.port;

import com.fincore.transaction.domain.model.Transfer;
import java.util.Optional;
import java.util.UUID;

public interface TransferRepository {

    Transfer save(Transfer transfer);

    Optional<Transfer> findById(UUID id);

    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);

    int updateStatus(UUID transferId, Transfer.TransferStatus status);

}
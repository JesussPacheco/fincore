package com.fincore.transaction.domain.port;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountServiceClient {

    void validateBalance(UUID accountId, BigDecimal amount, String currency);

    void credit(UUID accountId, BigDecimal amount, String currency);
}
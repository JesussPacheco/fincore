package com.fincore.transaction.infrastructure.web;

import com.fincore.transaction.domain.model.Transfer.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferRequest(

        @NotNull(message = "Source account is required")
        UUID sourceAccountId,

        @NotNull(message = "Target account is required")
        UUID targetAccountId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Currency is required")
        Currency currency
) {}
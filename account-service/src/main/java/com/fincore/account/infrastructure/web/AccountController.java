package com.fincore.account.infrastructure.web;

import com.fincore.account.application.service.AccountApplicationService;
import com.fincore.account.domain.model.Account.AccountType;
import com.fincore.account.domain.model.Account.Currency;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    private final AccountApplicationService accountApplicationService;

    public AccountController(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(
            @Valid @RequestBody CreateAccountRequest request,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Create account request userId={} type={} currency={}",
                userId, request.type(), request.currency());

        return AccountResponse.fromDomain(
                accountApplicationService.createAccount(
                        UUID.fromString(userId),
                        request.type(),
                        request.currency()
                )
        );
    }

    @GetMapping("/{accountId}/balance")
    public AccountResponse getBalance(
            @PathVariable UUID accountId,
            @RequestHeader("X-User-Id") String userId) {

        log.info("Get balance request accountId={} userId={}", accountId, userId);

        return AccountResponse.fromDomain(
                accountApplicationService.getBalance(accountId, UUID.fromString(userId))
        );
    }

    @PostMapping("/{accountId}/validate-balance")
    public ResponseEntity<Void> validateBalance(
            @PathVariable UUID accountId,
            @RequestBody Map<String, Object> request) {

        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency   = request.get("currency").toString();

        log.info("Validate balance request accountId={} amount={} currency={}",
                accountId, amount, currency);

        accountApplicationService.validateBalance(accountId, amount, currency);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{accountId}/credit")
    public ResponseEntity<Void> credit(
            @PathVariable UUID accountId,
            @RequestBody Map<String, Object> request) {

        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        String currency   = request.get("currency").toString();

        log.info("Credit request accountId={} amount={} currency={}",
                accountId, amount, currency);

        accountApplicationService.credit(accountId, amount, currency);
        return ResponseEntity.ok().build();
    }
}
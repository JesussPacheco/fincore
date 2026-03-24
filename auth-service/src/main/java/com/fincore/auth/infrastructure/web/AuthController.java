package com.fincore.auth.infrastructure.web;

import com.fincore.auth.domain.model.User;
import com.fincore.auth.domain.usecase.LoginUseCase;
import com.fincore.auth.domain.usecase.RegisterUserUseCase;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final RegisterUserUseCase registerUserUseCase;
    private final LoginUseCase loginUseCase;


    public AuthController(RegisterUserUseCase registerUserUseCase,
                          LoginUseCase loginUseCase) {
        this.registerUserUseCase = registerUserUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request received email={}", request.email());

        User user = registerUserUseCase.execute(
                request.name(),
                request.email(),
                request.password()
        );

        log.info("User registered successfully userId={}", user.getId());

        return RegisterResponse.fromDomain(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received email={}", request.email());

        String token = loginUseCase.execute(request.email(), request.password());

        log.info("Login successful email={}", request.email());

        return ResponseEntity.ok(LoginResponse.of(token, 900000L));
    }
}
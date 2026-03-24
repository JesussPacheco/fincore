package com.fincore.auth;

import com.fincore.auth.domain.port.EventPublisher;
import com.fincore.auth.domain.port.PasswordEncoder;
import com.fincore.auth.domain.port.TokenGenerator;
import com.fincore.auth.domain.port.UserRepository;
import com.fincore.auth.domain.usecase.LoginUseCase;
import com.fincore.auth.domain.usecase.RegisterUserUseCase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthApplication.class, args);
    }

    // Wires domain use case with infrastructure implementations
    @Bean
    public RegisterUserUseCase registerUserUseCase(
            UserRepository userRepository,
            EventPublisher eventPublisher,
            PasswordEncoder passwordEncoder) {
        return new RegisterUserUseCase(userRepository, eventPublisher, passwordEncoder);
    }

    @Bean
    public LoginUseCase loginUseCase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            TokenGenerator tokenGenerator) {
        return new LoginUseCase(userRepository, passwordEncoder, tokenGenerator);
    }
}
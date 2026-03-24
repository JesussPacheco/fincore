package com.fincore.auth.domain.usecase;

import com.fincore.auth.domain.exception.InvalidCredentialsException;
import com.fincore.auth.domain.model.User;
import com.fincore.auth.domain.port.PasswordEncoder;
import com.fincore.auth.domain.port.TokenGenerator;
import com.fincore.auth.domain.port.UserRepository;

public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenGenerator tokenGenerator;

    public LoginUseCase(UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        TokenGenerator tokenGenerator) {
        this.userRepository   = userRepository;
        this.passwordEncoder  = passwordEncoder;
        this.tokenGenerator   = tokenGenerator;
    }

    public String execute(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()))
                .filter(User::isActive)
                .map(tokenGenerator::generate)
                .orElseThrow(InvalidCredentialsException::new);
    }
}
package com.fincore.auth.domain.usecase;

import com.fincore.auth.domain.event.UserRegisteredEvent;
import com.fincore.auth.domain.exception.EmailAlreadyExistsException;
import com.fincore.auth.domain.model.User;
import com.fincore.auth.domain.port.EventPublisher;
import com.fincore.auth.domain.port.PasswordEncoder;
import com.fincore.auth.domain.port.UserRepository;

public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserUseCase(UserRepository userRepository,
                               EventPublisher eventPublisher,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.passwordEncoder = passwordEncoder;
    }

    public User execute(String name, String email, String rawPassword) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        String passwordHash = passwordEncoder.encode(rawPassword);
        User user = new User(name, email, passwordHash);
        User savedUser = userRepository.save(user);

        eventPublisher.publish(
                "fincore.user.registered",
                new UserRegisteredEvent(savedUser.getId(), savedUser.getEmail())
        );

        return savedUser;
    }
}
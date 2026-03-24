package com.fincore.auth.domain.port;

import com.fincore.auth.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

    User save(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
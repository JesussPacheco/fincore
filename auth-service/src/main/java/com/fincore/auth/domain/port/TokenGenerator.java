package com.fincore.auth.domain.port;

import com.fincore.auth.domain.model.User;

public interface TokenGenerator {

    String generate(User user);
}
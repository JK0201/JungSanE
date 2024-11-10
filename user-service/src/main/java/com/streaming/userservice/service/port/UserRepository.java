package com.streaming.userservice.service.port;

import com.streaming.userservice.entity.user.AuthProvider;
import com.streaming.userservice.entity.user.User;

import java.util.Optional;

public interface UserRepository {

    // READ
    Optional<User> findByProviderAndUsername(AuthProvider authProvider, String username);

    // CUD
    User save(User newUser);
}

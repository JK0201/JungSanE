package com.streaming.userservice.repository;

import com.streaming.userservice.entity.AuthProvider;
import com.streaming.userservice.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByProviderAndUsername(AuthProvider authProvider, String username);

    User save(User newUser);
}

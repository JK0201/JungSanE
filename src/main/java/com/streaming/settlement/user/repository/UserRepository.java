package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByAuthProviderAndUsername(AuthProvider authProvider, String username);

    User save(User newUser);
}

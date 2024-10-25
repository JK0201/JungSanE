package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.dto.User;
import com.streaming.settlement.user.entity.AuthProvider;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByAuthProviderAndUsername(AuthProvider authProvider, String username);

    User save(User newUser);
}

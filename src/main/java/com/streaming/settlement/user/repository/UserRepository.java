package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.dto.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(User newUser);
}

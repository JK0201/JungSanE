package com.streaming.settlement.repository;

import com.streaming.settlement.dto.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByUsername(String username);

    User save(User newUser);
}

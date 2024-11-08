package com.streaming.userservice.repository.user;

import com.streaming.userservice.entity.AuthProvider;
import com.streaming.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    // READ
    Optional<User> findByAuthProviderAndUsername(AuthProvider authProvider, String username);

    // CUD
}

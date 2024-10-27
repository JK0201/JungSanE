package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    Optional<User> findByAuthProviderAndUsername(AuthProvider authProvider, String username);
}

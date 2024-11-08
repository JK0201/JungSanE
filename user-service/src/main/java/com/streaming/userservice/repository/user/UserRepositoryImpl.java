package com.streaming.userservice.repository.user;

import com.streaming.userservice.entity.AuthProvider;
import com.streaming.userservice.entity.User;
import com.streaming.userservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    // READ
    @Override
    public Optional<User> findByProviderAndUsername(AuthProvider authProvider, String username) {
        return userJpaRepository.findByAuthProviderAndUsername(authProvider, username);
    }

    // CUD
    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}

package com.streaming.userservice.repository;

import com.streaming.userservice.entity.AuthProvider;
import com.streaming.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByAuthProviderAndUsername(AuthProvider authProvider, String username) {
        return userJpaRepository.findByAuthProviderAndUsername(authProvider, username);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }
}

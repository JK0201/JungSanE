package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.dto.User;
import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public Optional<User> findByAuthProviderAndUsername(AuthProvider authProvider, String username) {
        return userJpaRepository.findByAuthProviderAndUsername(authProvider, username).map(UserEntity::toModel);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.from(user)).toModel();
    }
}

package com.streaming.settlement.user.infrastructure;

import com.streaming.settlement.user.domain.User;
import com.streaming.settlement.user.domain.UserType;
import com.streaming.settlement.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(UserEntity.from(user)).toModel();
    }

    @Override
    public Optional<User> findByEmailAndUserType(String email, UserType userType) {
        return userJpaRepository.findByEmailAndUserType(email, userType)
                .map(UserEntity::toModel);
    }
}

package com.streaming.settlement.user.infrastructure;

import com.streaming.settlement.user.domain.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmailAndUserType(String email, UserType userType);

    Optional<UserEntity> findByEmail(String email);
}

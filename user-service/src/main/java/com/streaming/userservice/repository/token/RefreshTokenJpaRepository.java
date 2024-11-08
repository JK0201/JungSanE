package com.streaming.userservice.repository.token;

import com.streaming.userservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {

    // READ
    Optional<RefreshToken> findByUserIdAndRefreshToken(Long userId, String refreshToken);

    // CUD
    void deleteByRefreshToken(String refreshToken);
}

package com.streaming.userservice.repository;

import com.streaming.userservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long> {


    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserIdAndRefreshToken(Long userId, String refreshToken);
}

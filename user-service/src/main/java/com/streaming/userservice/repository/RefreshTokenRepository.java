package com.streaming.userservice.repository;

import com.streaming.userservice.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    void save(RefreshToken newRefreshToken);

    void deleteByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserIdAndRefreshToken(Long userId, String refreshToken);
}

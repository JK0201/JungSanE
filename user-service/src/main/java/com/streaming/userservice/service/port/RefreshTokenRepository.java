package com.streaming.userservice.service.port;

import com.streaming.userservice.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    // READ
    Optional<RefreshToken> findByUserIdAndRefreshToken(Long userId, String refreshToken);

    // CUD
    void save(RefreshToken newRefreshToken);

    void deleteByRefreshToken(String refreshToken);
}

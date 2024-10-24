package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.dto.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshTokenAndUsername(String username, String refreshToken);

    void save(RefreshToken newRefreshToken);

    void deleteByRefreshToken(String refreshToken);
}

package com.streaming.settlement.repository;

import com.streaming.settlement.dto.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshTokenAndUsername(String username, String refreshToken);

    void save(RefreshToken newRefreshToken);
}

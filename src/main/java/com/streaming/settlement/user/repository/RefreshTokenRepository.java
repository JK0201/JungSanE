package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshTokenFetchUser(AuthProvider authProvider, String username, String refreshToken);

    void save(RefreshToken newRefreshToken);

    void deleteByRefreshToken(String refreshToken);
}

package com.streaming.userservice.repository;

import com.streaming.userservice.entity.AuthProvider;
import com.streaming.userservice.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshTokenFetchUser(AuthProvider authProvider, String username, String refreshToken);

    void save(RefreshToken newRefreshToken);

    void deleteByRefreshToken(String refreshToken);
}

package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshToken> findByRefreshTokenFetchUser(AuthProvider authProvider, String username, String refreshToken) {
        return refreshTokenJpaRepository.findByRefreshTokenFetchUser(authProvider, username, refreshToken);
    }

    @Override
    public void save(RefreshToken newRefreshToken) {
        refreshTokenJpaRepository.save(newRefreshToken);
    }

    @Override
    @Transactional
    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByRefreshToken(refreshToken);
    }
}

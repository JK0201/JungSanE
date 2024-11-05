package com.streaming.userservice.repository;

import com.streaming.userservice.entity.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public void save(RefreshToken newRefreshToken) {
        refreshTokenJpaRepository.save(newRefreshToken);
    }

    @Override
    public Optional<RefreshToken> findByUserIdAndRefreshToken(Long userId, String refreshToken) {
        return refreshTokenJpaRepository.findByUserIdAndRefreshToken(userId, refreshToken);
    }

    @Override
    @Transactional
    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByRefreshToken(refreshToken);
    }
}

package com.streaming.userservice.repository.token;

import com.streaming.userservice.entity.RefreshToken;
import com.streaming.userservice.service.port.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    // READ
    @Override
    public Optional<RefreshToken> findByUserIdAndRefreshToken(Long userId, String refreshToken) {
        return refreshTokenJpaRepository.findByUserIdAndRefreshToken(userId, refreshToken);
    }

    // CUD
    @Override
    public void save(RefreshToken newRefreshToken) {
        refreshTokenJpaRepository.save(newRefreshToken);
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByRefreshToken(refreshToken);
    }
}

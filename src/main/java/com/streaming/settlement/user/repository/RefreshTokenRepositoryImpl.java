package com.streaming.settlement.user.repository;

import com.streaming.settlement.user.dto.RefreshToken;
import com.streaming.settlement.user.entity.RefreshTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository refreshTokenJpaRepository;

    @Override
    public Optional<RefreshToken> findByRefreshTokenAndUsername(String username, String refreshToken) {
        return refreshTokenJpaRepository.findByRefreshTokenAndUsername(username, refreshToken)
                .map(RefreshTokenEntity::toModel);
    }

    @Override
    public void save(RefreshToken newRefreshToken) {
        refreshTokenJpaRepository.save(RefreshTokenEntity.from(newRefreshToken));
    }

    @Override
    @Transactional
    public void deleteByRefreshToken(String refreshToken) {
        refreshTokenJpaRepository.deleteByRefreshToken(refreshToken);
    }
}

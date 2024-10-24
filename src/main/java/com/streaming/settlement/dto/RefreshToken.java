package com.streaming.settlement.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RefreshToken {

    private final Long id;
    private final String refreshToken;
    private final LocalDateTime expiryTime;
    private final User user;

    @Builder
    public RefreshToken(Long id, String refreshToken, LocalDateTime expiryTime, User user) {
        this.id = id;
        this.refreshToken = refreshToken;
        this.expiryTime = expiryTime;
        this.user = user;
    }


    public static RefreshToken fromCreatedToken(String newRefreshToken, Long expiryTime, User user) {
        return RefreshToken.builder()
                .refreshToken(newRefreshToken)
                .expiryTime(LocalDateTime.now().plusSeconds(expiryTime / 1000))
                .user(user)
                .build();
    }

    public static RefreshToken update(RefreshToken existRefreshToken, String newRefreshToken, Long expiryTime) {
        return RefreshToken.builder()
                .id(existRefreshToken.getId())
                .refreshToken(newRefreshToken)
                .expiryTime(LocalDateTime.now().plusSeconds(expiryTime / 1000))
                .user(existRefreshToken.getUser())
                .build();
    }
}

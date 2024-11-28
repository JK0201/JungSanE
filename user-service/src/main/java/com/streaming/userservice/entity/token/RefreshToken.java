package com.streaming.userservice.entity.token;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "refreshTokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    public static RefreshToken of(String createdRefreshToken, LocalDateTime expiryTime, Long userId) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.refreshToken = createdRefreshToken;
        refreshToken.expiryTime = expiryTime;
        refreshToken.userId = userId;
        return refreshToken;
    }

    /**
     * 새로 발급 받은 토큰으로 업데이트
     *
     * @param newRefreshToken 새로 발급 받은 토큰 (String)
     * @param expiryTime      만료 시간 (String)
     */
    public void update(String newRefreshToken, LocalDateTime expiryTime) {
        this.refreshToken = newRefreshToken;
        this.expiryTime = expiryTime;
    }
}

package com.streaming.settlement.user.entity;

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

    @Column(nullable = false, length = 500)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static RefreshToken fromCreatedToken(String createdRefreshToken, Long expiryTime, User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.refreshToken = createdRefreshToken;
        refreshToken.expiryTime = LocalDateTime.now().plusSeconds(expiryTime / 1000);
        refreshToken.user = user;
        return refreshToken;
    }

    public void update(String newRefreshToken, Long expiryTime) {
        this.refreshToken = newRefreshToken;
        this.expiryTime = LocalDateTime.now().plusSeconds(expiryTime / 1000);
    }
}

package com.streaming.settlement.user.entity;

import com.streaming.settlement.user.dto.RefreshToken;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "refreshToken")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id")
    private Long id;

    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private LocalDateTime expiryTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    public static RefreshTokenEntity from(RefreshToken newRefreshToken) {
        RefreshTokenEntity refreshTokenEntity = new RefreshTokenEntity();
        refreshTokenEntity.id = newRefreshToken.getId();
        refreshTokenEntity.refreshToken = newRefreshToken.getRefreshToken();
        refreshTokenEntity.expiryTime = newRefreshToken.getExpiryTime();
        refreshTokenEntity.userEntity = UserEntity.from(newRefreshToken.getUser());

        return refreshTokenEntity;
    }

    public RefreshToken toModel() {
        return RefreshToken.builder()
                .id(id)
                .refreshToken(refreshToken)
                .expiryTime(expiryTime)
                .user(userEntity.toModel())
                .build();
    }
}

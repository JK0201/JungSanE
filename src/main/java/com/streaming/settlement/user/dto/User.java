package com.streaming.settlement.user.dto;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final String username;
    private final String nickname;
    private final UserRole role;
    private final AuthProvider authProvider;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public User(Long id, String email, String username, String nickname, UserRole role, AuthProvider authProvider, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.authProvider = authProvider;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static User fromOAuth(String username, OAuth2Response oAuth2Response, AuthProvider authProvider) {
        return User.builder()
                .email(oAuth2Response.getEmail())
                .username(username)
                .nickname(oAuth2Response.getName())
                .role(UserRole.UPLOADER) // FIXME 테스트 후 권한 설정 변경
                .authProvider(authProvider)
                .build();
    }

    public static User fromToken(String username, UserRole role, AuthProvider authProvider) {
        return User.builder()
                .username(username)
                .role(role)
                .authProvider(authProvider)
                .build();
    }
}

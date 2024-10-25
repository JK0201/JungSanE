package com.streaming.settlement.user.dto;

import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final String username;
    private final String nickname;
    private final UserRole role;
    private final AuthProvider authProvider;

    @Builder
    public User(Long id, String email, String username, String nickname, UserRole role, AuthProvider authProvider) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.authProvider = authProvider;
    }

    public static User fromOAuth(String username, OAuth2Response oAuth2Response, AuthProvider authProvider) {
        return User.builder()
                .email(oAuth2Response.getEmail())
                .username(username)
                .nickname(oAuth2Response.getName())
                .role(UserRole.USER)
                .authProvider(authProvider)
                .build();
    }

    public static User fromToken(String username, UserRole role) {
        return User.builder()
                .username(username)
                .role(role)
                .build();
    }
}

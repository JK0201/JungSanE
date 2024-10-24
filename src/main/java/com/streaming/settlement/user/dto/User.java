package com.streaming.settlement.user.dto;

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

    @Builder
    public User(Long id, String email, String username, String nickname, UserRole role) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
    }

    public static User fromOAuth(String username, OAuth2Response oAuth2Response) {
        return User.builder()
                .email(oAuth2Response.getEmail())
                .username(username)
                .nickname(oAuth2Response.getName())
                .role(UserRole.USER)
                .build();
    }

    public static User fromToken(String username, UserRole role) {
        return User.builder()
                .username(username)
                .role(role)
                .build();
    }
}

package com.streaming.settlement.user.domain;

import com.streaming.settlement.user.infrastructure.kakao.KakaoUserInfoResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
public class User {

    private final Long id;
    private final String email;
    private final String username;
    private final UserRole role;
    private final UserType userType;

    @Builder
    public User(Long id, String email, String username, UserRole role, UserType userType) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.role = role;
        this.userType = userType;
    }
    
    public static User from(KakaoUserInfoResponse userInfo) {
        return User.builder()
                .email(userInfo.getEmail())
                .username(userInfo.getUsername())
                .role(UserRole.USER)
                .userType(UserType.KAKAO)
                .build();
    }
}

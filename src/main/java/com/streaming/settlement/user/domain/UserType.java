package com.streaming.settlement.user.domain;

import lombok.Getter;

@Getter
public enum UserType {

    KAKAO("카카오 소셜 로그인");

    private final String domain;

    UserType(String domain) {
        this.domain = domain;
    }
}

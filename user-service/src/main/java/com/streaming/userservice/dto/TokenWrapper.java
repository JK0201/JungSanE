package com.streaming.userservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenWrapper {

    private String accessToken;
    private String refreshToken;

    public static TokenWrapper from(String accessToken, String refreshToken) {
        return TokenWrapper.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

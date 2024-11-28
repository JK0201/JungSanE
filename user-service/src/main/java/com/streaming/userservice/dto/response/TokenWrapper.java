package com.streaming.userservice.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenWrapper {

    private String accessToken;
    private String refreshToken;

    public static TokenWrapper of(String accessToken, String refreshToken) {
        return TokenWrapper.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}

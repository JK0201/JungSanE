package com.streaming.settlement.user.infrastructure.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
public class KakaoAuthorizationResponse {

    @JsonProperty("token_type")
    private final String tokenType;

    @JsonProperty("access_token")
    private final String accessToken;

    @JsonProperty("expires_in")
    private final Integer expiresIn;

    @JsonProperty("refresh_token")
    private final String refreshToken;

    @JsonProperty("refresh_token_expires_in")
    private final Integer refreshTokenExpiresIn;

    @Builder
    public KakaoAuthorizationResponse(String tokenType, String accessToken, Integer expiresIn, String refreshToken, Integer refreshTokenExpiresIn) {
        this.tokenType = tokenType;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.refreshToken = refreshToken;
        this.refreshTokenExpiresIn = refreshTokenExpiresIn;
    }
}

package com.streaming.common.constant;

public class JwtConstant {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_ROLE = "role";
    public static final String TOKEN_PREFIX = "Bearer ";
    // FIXME 토큰 시간 변경 요망
    public static final Long ACCESS_TOKEN_EXPIRY_TIME = 864000000L; // Access Token 10mins
    public static final Long REFRESH_TOKEN_EXPIRY_TIME = 864000000L; // Refresh Token 24hrs
}

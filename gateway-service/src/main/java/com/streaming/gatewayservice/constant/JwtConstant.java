package com.streaming.gatewayservice.constant;

public class JwtConstant {
    
    public static class JwtToken {
        public static final String REFRESH_TOKEN = "refresh_token";
        public static final String GRANT_TYPE = "grant_type";
        public static final String CLAIM_USER_ID = "user_id";
        public static final String CLAIM_ROLE = "role";
        public static final String TOKEN_PREFIX = "Bearer ";
    }

    public static class RequestHeader {
        public static final String USER_ID_HEADER = "X-User-Id";
    }
}

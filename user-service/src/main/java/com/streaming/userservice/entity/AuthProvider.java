package com.streaming.userservice.entity;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum AuthProvider {

    GOOGLE("GOOGLE"),
    NAVER("NAVER");

    private final String provider;

    AuthProvider(String provider) {
        this.provider = provider;
    }

    // Map을 사용하여 유저 권한을 인메모리 캐싱
    private static final Map<String, AuthProvider> ROLE_MAP = new HashMap<>();

    static {
        for (AuthProvider authProvider : AuthProvider.values()) {
            ROLE_MAP.put(authProvider.getProvider(), authProvider);
        }
    }

    // 요청이 들어왔을때 O(1)로 조회하여 UserRole 반환
    public static AuthProvider fromProvider(String provider) {
        return ROLE_MAP.get(provider);
    }
}

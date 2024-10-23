package com.streaming.settlement.entity;//package com.streaming.settlement.user.domain;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum UserRole {

    USER("ROLE_USER"),
    UPLOADER("ROLE_UPLOADER");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }

    // Map을 사용하여 유저 권한을 인메모리 캐싱
    private static final Map<String, UserRole> ROLE_MAP = new HashMap<>();

    static {
        for (UserRole role : UserRole.values()) {
            ROLE_MAP.put(role.getAuthority(), role);
        }
    }

    // 요청이 들어왔을때 O(1)로 조회하여 UserRole 반환
    public static UserRole fromAuthority(String role) {
        UserRole userRole = ROLE_MAP.get(role);
        if (userRole == null) {
            throw new IllegalArgumentException("잘못된 유저 등급입니다. : " + role);
        }

        return userRole;
    }
}

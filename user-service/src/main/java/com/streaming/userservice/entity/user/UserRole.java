package com.streaming.userservice.entity.user;

import lombok.Getter;

@Getter
public enum UserRole {

    USER("ROLE_USER"),
    UPLOADER("ROLE_UPLOADER"),
    ADMIN("ROLE_ADMIN");

    private final String authority;

    UserRole(String authority) {
        this.authority = authority;
    }
}

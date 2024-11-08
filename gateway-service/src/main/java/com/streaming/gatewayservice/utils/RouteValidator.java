package com.streaming.gatewayservice.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import static com.streaming.gatewayservice.constant.ApiPath.protectedApi;
import static com.streaming.gatewayservice.constant.ApiPath.publicApi;

@Component
public class RouteValidator {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 요청중, 공개 API 경로에 대한 검증
     *
     * @param path API 경로 (String)
     * @return boolean
     */
    public boolean isPublicRoute(String path) {
        return publicApi.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, path));
    }

    /**
     * 요청중, 권한이 필요한 API 경로에 대한 권한 검증
     *
     * @param path API 경로 (String)
     * @param role 권한 (String)
     * @return boolean
     */
    public boolean hasPermission(String path, String role) {
        return protectedApi.entrySet().stream()
                .filter(entry -> antPathMatcher.match(entry.getKey(), path))
                .findFirst()
                .map(entry -> entry.getValue().contains(role))
                .orElse(true);
    }
}

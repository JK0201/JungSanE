package com.streaming.gatewayservice.constant;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApiPath {
    // API Gateway
    public static class GatewayPath {
        public static final String USER_SERVICE = "/user-service/**";
        public static final String VIDEO_SERVICE = "/video-service/**";
        public static final String ADJUSTMENT_SERVICE = "/adjustment-service/**";
    }

    // Load Balancer URI
    public static class LoadBalancerUri {
        public static final String USER_SERVICE_LB = "lb://user-service";
        public static final String TOKEN_REISSUE_LB = "lb://user-service/auth/reissue";
        public static final String VIDEO_SERVICE_LB = "lb://video-service";
        public static final String ADJUSTMENT_SERVICE_LB = "lb://adjustment-service";
    }

    // Public API (공개 경로)
    public static final List<String> publicApi = List.of(
            "/user-service/auth/**",
            "/user-service/oauth2/**",
            "/user-service/login/**"
    );

    // Protected API (권한별 경로)
    public static final Map<String, Set<String>> protectedApi =
            Map.of(
                    "video-service/api/v*/video/publish"
                    , Set.of("ROLE_UPLOADER", "ROLE_ADMIN")
            );
}

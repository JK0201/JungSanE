package com.streaming.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.streaming.gatewayservice.constant.ApiPath.GatewayPath.*;
import static com.streaming.gatewayservice.constant.ApiPath.LoadBalancerUri.*;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("user-service", r -> r
                        .path(USER_SERVICE)
                        .filters(f -> f.stripPrefix(1))
                        .uri(USER_SERVICE_LB))
                .route("video-service", r -> r
                        .path(VIDEO_SERVICE)
                        .filters(f -> f.stripPrefix(1))
                        .uri(VIDEO_SERVICE_LB))
                .route("adjustment-service", r -> r
                        .path(ADJUSTMENT_SERVICE)
                        .filters(f -> f.stripPrefix(1))
                        .uri(ADJUSTMENT_SERVICE_LB))
                .build();
    }
}

package com.streaming.gatewayservice.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("user-service", r -> r.path("/user-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))
                .route("video-service", r -> r.path("/video-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://video-service"))
                .route("adjustment-service", r -> r.path("/adjustment-service/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://adjustment-service"))
                .build();
    }
}

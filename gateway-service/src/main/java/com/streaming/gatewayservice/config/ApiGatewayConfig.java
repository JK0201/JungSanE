package com.streaming.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.streaming.gatewayservice.constant.ApiPath.GatewayPath.*;
import static com.streaming.gatewayservice.constant.ApiPath.LoadBalancerUri.*;

@Slf4j
@Configuration
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                .route("user-service", r -> r
                        .path(USER_SERVICE)
                        .filters(f -> {
                            log.info("Gateway Filter - Before Route");
                            return f.stripPrefix(1)
                                    .filter((exchange, chain) -> {
                                        String path = exchange.getRequest().getPath().toString();
                                        String method = exchange.getRequest().getMethod().toString();
                                        String uri = exchange.getRequest().getURI().toString();

                                        log.info("Gateway Incoming Request - Method: {}, Path: {}, URI: {}",
                                                method, path, uri);

                                        return chain.filter(exchange);
                                    });
                        })
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

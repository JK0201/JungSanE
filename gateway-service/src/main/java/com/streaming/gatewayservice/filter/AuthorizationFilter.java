package com.streaming.gatewayservice.filter;

import com.streaming.gatewayservice.response.ResponseHandler;
import com.streaming.gatewayservice.utils.RouteValidator;
import com.streaming.gatewayservice.utils.TokenHandler;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.streaming.gatewayservice.constant.JwtConstant.JwtToken.CLAIM_ROLE;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorizationFilter implements GlobalFilter, Ordered {

    private final RouteValidator routeValidator;
    private final TokenHandler tokenHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 공개 경로면 다음 필터 진행
        if (routeValidator.isPublicRoute(path)) return chain.filter(exchange);

        // 이전 필터에서 attributes에 저장한 claims 추출
        Claims claims = exchange.getAttribute("claims");
        if (claims == null) {
            return ResponseHandler.unauthorized(exchange.getResponse(), "Missing token claims");
        }

        // 특정 권한이 필요한 요청에 대해 검증
        String role = claims.get(CLAIM_ROLE, String.class);
        if (!routeValidator.hasPermission(path, role)) {
            return ResponseHandler.forbidden(
                    exchange.getResponse(),
                    "Access denied: insufficient permissions"
            );
        }
        return tokenHandler.validToken(exchange, chain, claims);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}

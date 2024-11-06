package com.streaming.gatewayservice.filter;

import com.streaming.gatewayservice.config.JwtUtil;
import com.streaming.gatewayservice.response.ResponseHandler;
import com.streaming.gatewayservice.utils.RouteValidator;
import com.streaming.gatewayservice.utils.TokenHandler;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.streaming.gatewayservice.constant.JwtConstant.JwtToken.GRANT_TYPE;
import static com.streaming.gatewayservice.constant.JwtConstant.JwtToken.TOKEN_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final RouteValidator routeValidator;
    private final TokenHandler tokenHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();

        // 공개 경로면 다음 필터 진행
        if (routeValidator.isPublicRoute(path)) return chain.filter(exchange);

        // 헤더 검증
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            // Access Token이 없을 경우, Refresh Token을 사용하여 재발급
            return tokenHandler.expiredAccessToken(exchange, chain);
        }

        try {
            // Access Token 검증 및 추출
            String accessToken = authHeader.substring(7); // "Bearer " 이후 뒤쪽의 토큰을 반환
            Claims claims = jwtUtil.validateAndExtractClaims(accessToken);
            String tokenType = claims.get(GRANT_TYPE, String.class);

            // Grant_type (토큰 타입)이 Access Token이 아닐경우 에러 응답
            if (!tokenType.equals("ACCESS")) {
                return ResponseHandler.unauthorized(
                        exchange.getResponse(),
                        "Invalid token type: expected Access Token");
            }
            // exchange attributes에 claims 저장
            exchange.getAttributes().put("claims", claims);
            return chain.filter(exchange);
        } catch (ExpiredJwtException ex) {
            // Access Token 만료시, Refresh Token을 사용하여 재발급
            return tokenHandler.expiredAccessToken(exchange, chain);
        } catch (Exception ex) {
            return ResponseHandler.unauthorized(
                    exchange.getResponse(),
                    "Invalid Access Token");
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}

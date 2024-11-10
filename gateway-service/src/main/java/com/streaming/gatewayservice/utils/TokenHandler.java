package com.streaming.gatewayservice.utils;

import com.streaming.gatewayservice.config.JwtUtil;
import com.streaming.gatewayservice.response.ResponseHandler;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.streaming.gatewayservice.constant.ApiPath.REISSUE_URI;
import static com.streaming.gatewayservice.constant.JwtConstant.JwtToken.*;
import static com.streaming.gatewayservice.constant.JwtConstant.RequestHeader.USER_ID_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenHandler {

    private final JwtUtil jwtUtil;
    private final WebClient userServiceWebClient;

    /**
     * 토큰 재발급을 위해 RefreshToken Cookie에서 추출
     *
     * @param exchange (ServerWebExchange)
     * @param chain    (GatewayFilterChain)
     */
    public Mono<Void> expiredAccessToken(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpCookie refreshTokenCookie = exchange.getRequest()
                .getCookies()
                .getFirst(REFRESH_TOKEN);

        if (refreshTokenCookie == null) {
            return ResponseHandler.unauthorized(
                    exchange.getResponse(),
                    "No refresh token available for reissue");
        }

        return reissue(exchange, chain, refreshTokenCookie.getValue());
    }

    /**
     * WebClient를 사용하여, reissue 경로로 Access Token과 Refresh Token 재발급 요청
     *
     * @param exchange     (ServerWebExchange)
     * @param chain        (GatewayFilterChain)
     * @param refreshToken (String)
     */
    public Mono<Void> reissue(ServerWebExchange exchange, GatewayFilterChain chain, String refreshToken) {
        return userServiceWebClient.post()
                .uri(REISSUE_URI)
                .cookie(REFRESH_TOKEN, refreshToken)
                .retrieve()
                .toEntity(Void.class)
                .flatMap(response -> {
                    // 새로운 Access Token 추출
                    String newAccessToken = response.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    if (newAccessToken == null) {
                        return ResponseHandler.unauthorized(
                                exchange.getResponse(),
                                "Token reissue failed");
                    }

                    try {
                        // 새로 받은 Access Token 검증 및 Claims 추출
                        Claims newClaims = jwtUtil.validateAndExtractClaims(newAccessToken);
                        // exchange attributes에 새로운 claims 저장
                        exchange.getAttributes().put("claims", newClaims);

                        // 새로 받은 Access Token으로 Header 수정
                        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                                .header(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + newAccessToken)
                                .build();

                        // 클라이언트에게 새 Access Token 전달 (Header)
                        exchange.getResponse().getHeaders()
                                .add(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + newAccessToken);

                        // 클라이언트에게 새 Refresh Token 전달 (Cookie)
                        List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
                        if (cookies != null && !cookies.isEmpty()) {
                            exchange.getResponse().getHeaders().put(HttpHeaders.SET_COOKIE, cookies);
                        }

                        // 다음 필터 진행
                        return chain.filter(exchange.mutate()
                                .request(modifiedRequest)
                                .build());
                    } catch (Exception ex) {
                        return ResponseHandler.unauthorized(
                                exchange.getResponse(),
                                "Invalid reissued Access Token");
                    }

                })
                .onErrorResume(ex -> ResponseHandler.unauthorized(
                        exchange.getResponse(),
                        "Token reissue failed"
                ));
    }

    /**
     * Access Token이 유효할 경우, 헤더에 userId를 추가하여 MS에 요청
     *
     * @param exchange (ServerWebExchange)
     * @param chain    (GatewayFilterChain)
     * @param claims   (claims)
     */
    public Mono<Void> validToken(ServerWebExchange exchange, GatewayFilterChain chain, Claims claims) {
        ServerHttpRequest request = exchange.getRequest().mutate()
                .header(USER_ID_HEADER, String.valueOf(claims.get(CLAIM_USER_ID)))
                .build();

        return chain.filter(exchange.mutate().request(request).build());
    }
}

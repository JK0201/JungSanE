package com.streaming.settlement.common.config.jwt;

import com.streaming.settlement.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private static Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @BeforeEach
    void init() {
        String secretKey = "6rCc7J247ZSE66Gc7KCd7Yq4and07YWM7Iqk7Yq466W87JyE7ZWc7YWM7Iqk7Yq47YKk7IOd7ISx7ZW07ISc7J247L2U65Sp7ZWY6riw";
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    @Test
    void 로그인_요청이_들어오면_Access_Token을_생성() {
        // given
        String email = "example@example.com";
        UserRole role = UserRole.USER;

        String AUTHORIZATION_KEY = "auth";
        String BEARER_PREFIX = "Bearer ";
        long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L;

        Date now = new Date();

        // when
        String token = BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();

        // then
        assertThat(token).isNotNull();
    }

    @Test
    void Bearer와_Token을_분리하여_Token만_반환() {
        // given
        String email = "example@example.com";
        UserRole role = UserRole.USER;

        String AUTHORIZATION_KEY = "auth";
        String BEARER_PREFIX = "Bearer ";
        long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L;

        Date now = new Date();

        // when
        String token = BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();

        String jwtToken = token.substring(7);

        assertThat(token.substring(7)).isEqualTo(jwtToken);
    }

    @Test
    void Token에_저장된_정보_및_시간_조회_및_검증() {
        // given
        String email = "example@example.com";
        UserRole role = UserRole.USER;

        String AUTHORIZATION_KEY = "auth";
        String BEARER_PREFIX = "Bearer ";
        long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L;

        Date now = new Date();

        // when
        String token = BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(email)
                        .claim(AUTHORIZATION_KEY, role)
                        .setExpiration(new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME))
                        .setIssuedAt(now)
                        .signWith(key, signatureAlgorithm)
                        .compact();

        String jwtToken = token.substring(7);

        Claims info = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();

        // then
        long expirationTime = info.getExpiration().getTime();
        long issuedAtTime = info.getIssuedAt().getTime();
        long timeDifference = expirationTime - issuedAtTime;

        String userRole = info.get(AUTHORIZATION_KEY, String.class);

        assertThat(info.getSubject()).isEqualTo("example@example.com");
        assertThat(timeDifference).isEqualTo(3600 * 1000L); // 3600초
        assertThat(userRole).isEqualTo("USER");
    }


}
package com.streaming.settlement.jwt;

import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "Jwt 토큰 Provider")
@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret.key}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String AUTHORIZATION_KEY = "auth";
    private final String BEARER_PREFIX = "Bearer ";

    /**
     * 토큰 생성
     *
     * @param username (String)
     * @param role     (UserRole)
     * @return JWT Token (String)
     */
    public String generateToken(String username, String role, Long expireTime) {
        return BEARER_PREFIX +
                Jwts.builder()
                        .claim("username", username)
                        .claim(AUTHORIZATION_KEY, role)
                        .issuedAt(new Date(System.currentTimeMillis()))
                        .expiration(new Date(System.currentTimeMillis() + expireTime))
                        .signWith(secretKey)
                        .compact();
    }

    /**
     * Header로 받아온 토큰을 "Bearer "와 분리하여 반환
     *
     * @param request (HttpServletRequest)
     * @return JWT Token (String)
     */
    public String tokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7); // "Bearer " 이후 뒤쪽의 토큰을 반환
        }

        // 헤더에 없을 경우 쿠키에서 토큰 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AUTHORIZATION_HEADER.equals(cookie.getName())) {
                    String cookieValue = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                    if (cookieValue.startsWith(BEARER_PREFIX)) {
                        return cookieValue.substring(7); // Bearer 이후 토큰 반환
                    }
                }
            }
        }

        return null;
    }

    /**
     * 토큰 검증 로직
     *
     * @param token (String)
     * @return boolean
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.", ex);
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", ex);
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.", ex);
        }

        return false;
    }

    /**
     * 토큰에서 사용자 정보 가져오기
     *
     * @param token (String)
     * @return Claims
     */
    public Claims userInformationFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

//    public String getUsername(String token) {
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get("username", String.class);
//    }
//
//    public String getRole(String token) {
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .get(AUTHORIZATION_KEY, String.class);
//    }
//
//    public Boolean isExpired(String token) {
//        return Jwts.parser()
//                .verifyWith(secretKey)
//                .build()
//                .parseSignedClaims(token)
//                .getPayload()
//                .getExpiration()
//                .before(new Date());
//    }
}

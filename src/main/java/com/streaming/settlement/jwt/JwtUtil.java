package com.streaming.settlement.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = " 토큰 Provider")
@Component
public class JwtUtil {

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret.key}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLAIM_USERNAME = "username";
    public static final String CLAIM_ROLE = "role";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final Long ACCESS_TOKEN_EXPIRY_TIME = 1000L;
    public static final Long REFRESH_TOKEN_EXPIRY_TIME = 86400000L; // Refresh Token 24hrs

    /**
     * 토큰 생성
     *
     * @param username (String)
     * @param role     (UserRole)
     * @return JWT Token (String)
     */
    public String generateToken(String tokenType, String username, String role, Long expireTime) {
        return Jwts.builder()
                .claim(GRANT_TYPE, tokenType)
                .claim(CLAIM_USERNAME, username)
                .claim(CLAIM_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Header로 받아온 Access Token을 "Bearer "와 분리하여 반환
     *
     * @param request (HttpServletRequest)
     * @return Access Token (String)
     */
    public String getAccessTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7); // "Bearer " 이후 뒤쪽의 토큰을 반환
        }

        return null;
    }

    /**
     * Cookie에서 Refresh Token을 찾아서 반환
     *
     * @param request (HttpServletRequest)
     * @return Refresh Token (String)
     */
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (AUTHORIZATION_HEADER.equals(cookie.getName())) {
                    return cookie.getValue();
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
            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
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
}

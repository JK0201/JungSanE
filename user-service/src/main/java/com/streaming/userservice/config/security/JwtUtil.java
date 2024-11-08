package com.streaming.userservice.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret.key}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN = "refresh_token";
    public static final String GRANT_TYPE = "grant_type";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_ROLE = "role";
    // FIXME 토큰 시간 변경 요망
    public static final Long ACCESS_TOKEN_EXPIRY_TIME = 864000000L; // Access Token 10mins
    public static final Long REFRESH_TOKEN_EXPIRY_TIME = 864000000L; // Refresh Token 24hrs

    /**
     * Access Token 생성
     *
     * @param userId (String)
     * @param role   (String)
     * @return Access Token (String)
     */
    public String generateAccessToken(String userId, String role) {
        return generateToken("ACCESS", userId, role, ACCESS_TOKEN_EXPIRY_TIME);
    }

    /**
     * Refresh Token 생성
     *
     * @param userId (String)
     * @param role   (String)
     * @return Refresh Token (String)
     */
    public String generateRefreshToken(String userId, String role) {
        return generateToken("REFRESH", userId, role, REFRESH_TOKEN_EXPIRY_TIME);
    }

    /**
     * 토큰 생성
     *
     * @param tokenType  ACCESS / REFRESH (String)
     * @param userId     (String)
     * @param role       (UserRole)
     * @param expireTime (Long)
     * @return JWT Token (String)
     */
    public String generateToken(String tokenType, String userId, String role, Long expireTime) {
        return Jwts.builder()
                .claim(GRANT_TYPE, tokenType)
                .claim(CLAIM_USER_ID, userId)
                .claim(CLAIM_ROLE, role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Refresh Token Cookie 생성
     *
     * @param refreshToken (String)
     * @return ResponseCookie
     */
    public ResponseCookie createCookie(String refreshToken, Long expiryTime) {
        return ResponseCookie.from(REFRESH_TOKEN, refreshToken)
                .maxAge(expiryTime)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Lax")
                .build();
    }

    /**
     * 토큰 검증 후, 사용자 정보 추출,
     *
     * @param token Refresh Token (String)
     * @return Claims (토큰내 유저 Claim 정보)
     */
    public Claims validateAndExtractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Refresh Token 만료 일자 추출
     *
     * @param refreshToken (String)
     * @return LocalDateTime
     */
    public LocalDateTime getExpiryTimeFromRefreshToken(String refreshToken) {
        Date expiryDate = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(refreshToken)
                .getPayload()
                .getExpiration();

        return expiryDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}

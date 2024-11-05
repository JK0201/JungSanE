package com.streaming.gatewayservice.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret.key}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        secretKey = new SecretKeySpec(decodedKey, "HmacSHA256");
    }

    /**
     * 토큰 검증 후, 사용자 정보 추출,
     *
     * @param token Access Token (String)
     * @return Claims (토큰내 유저 Claim 정보)
     */
    public Claims validateAndExtractClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
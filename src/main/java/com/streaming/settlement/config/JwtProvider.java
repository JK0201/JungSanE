//package com.streaming.settlement.common.config;
//
//import com.streaming.settlement.entity.UserRole;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import io.jsonwebtoken.security.SecurityException;
//import jakarta.annotation.PostConstruct;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//
//import java.security.Key;
//import java.util.Base64;
//import java.util.Date;
//
//@Slf4j(topic = "JWT 토큰 Provider")
//@Component
//public class JwtProvider {
//
//    public static final String AUTHORIZATION_HEADER = "Authorization";
//    private static final String AUTHORIZATION_KEY = "auth";
//    private static final String BEARER_PREFIX = "Bearer ";
//    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L; // Access Token 60분
//    private static final long REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 7 * 1000L; // Refresh Token 7일
//
//    @Value("${jwt.secret.key}")
//    private String secretKey;
//    private Key key;
//    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
//
//    @PostConstruct
//    public void init() {
//        byte[] bytes = Base64.getDecoder().decode(secretKey);
//        key = Keys.hmacShaKeyFor(bytes);
//    }
//
//    /**
//     * 토큰 생성
//     *
//     * @param email (String)
//     * @param role  (UserRole)
//     * @return "Bearer " + "JWT Token" (String)
//     */
//    public String generateToken(String email, UserRole role) {
//        Date date = new Date();
//
//        // Access Token 생성
//        return BEARER_PREFIX +
//                Jwts.builder()
//                        .setSubject(email) // 사용자 식별자값(email)
//                        .claim(AUTHORIZATION_KEY, role) // 유저 권한
//                        .setExpiration(new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME)) // 만료 시간
//                        .setIssuedAt(date) // 발급일
//                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
//                        .compact();
//    }
//
//    /**
//     * Header로 받아온 토큰을 Bearer와 분리하여 반환
//     *
//     * @param request (HttpServletRequest)
//     * @return JWT Token (String)
//     */
//    public String tokenFromHeader(HttpServletRequest request) {
//        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
//            return bearerToken.substring(7); // "Bearer " 이후 뒤쪽의 토큰을 반환
//        }
//        return null;
//    }
//
//    /**
//     * 토큰 검증 로직
//     *
//     * @param token (String)
//     * @return boolean
//     */
//    public boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder()
//                    .setSigningKey(key)
//                    .build()
//                    .parseClaimsJws(token);
//
//            return true;
//        } catch (SecurityException | MalformedJwtException ex) {
//            log.error("Invalid JWT signature, 유효하지 않은 JWT 서명 입니다.");
//        } catch (ExpiredJwtException ex) {
//            log.error("Expired JWT token, 만료된 JWT token 입니다.");
//        } catch (UnsupportedJwtException ex) {
//            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.");
//        } catch (IllegalStateException ex) {
//            log.error("JWT claims is empty, 잘못된 JWT 토큰 입니다.");
//        }
//
//        return false;
//    }
//
//    /**
//     * 토큰에서 사용자 정보 가져오기
//     *
//     * @param token (String)
//     * @return Claims
//     */
//    public Claims userInformationFromToken(String token) {
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }
//}

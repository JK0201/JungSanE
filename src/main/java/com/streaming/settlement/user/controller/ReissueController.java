package com.streaming.settlement.user.controller;

import com.streaming.settlement.user.config.JwtUtil;
import com.streaming.settlement.user.entity.AuthProvider;
import com.streaming.settlement.user.entity.RefreshToken;
import com.streaming.settlement.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.streaming.settlement.user.config.JwtUtil.*;

@Slf4j(topic = "JWT Token 재발급")
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
    @Transactional
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        // Refresh Token 쿠키에서 가져오기
        String authorization = jwtUtil.getRefreshTokenFromCookie(request);

        log.info("요청 토큰 = {}", authorization);

        // 토큰이 없을 경우 -> 에러 응답
        if (!StringUtils.hasText(authorization)) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 유효하지 않은 경우 -> 에러 응답
        if (!jwtUtil.validateToken(authorization)) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 유요할 경우 토큰에서 유저 정보를 가져옴
        Claims userInfo = jwtUtil.userInformationFromToken(authorization);
        String tokenType = userInfo.get(GRANT_TYPE, String.class);
        String username = userInfo.get(CLAIM_USERNAME, String.class);
        String role = userInfo.get(CLAIM_ROLE, String.class);
        String authProvider = userInfo.get(CLAIM_PROVIDER, String.class);

        // Payload에 Refresh Token이 아닌 Access Token을 넣었을 경우 -> 에러 응답
        if (!tokenType.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 Refresh Token이 저장되어 있는지 확인 -> Refresh Token이 없을 경우 에러 응답
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository
                .findByRefreshTokenFetchUser(AuthProvider.fromProvider(authProvider), username, authorization);
        if (existRefreshToken.isEmpty()) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        log.info("요청 유저 = username : {}, role : {}", username, role);

        // Access Token, Refresh Token 생성
        String newAccessToken = jwtUtil.generateToken("access", username, role, authProvider, ACCESS_TOKEN_EXPIRY_TIME);
        String newRefreshToken = jwtUtil.generateToken("refresh", username, role, authProvider, REFRESH_TOKEN_EXPIRY_TIME);

        // 새로 발급 받은 Refresh Token DB에 업데이트 (Dirty Checking)
        existRefreshToken.get().update(newRefreshToken, REFRESH_TOKEN_EXPIRY_TIME);
//        refreshTokenRepository.save(refreshToken);

        response.setHeader(AUTHORIZATION_HEADER, newAccessToken);
        response.addCookie(createCookie(newRefreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String cookieValue) {
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue);
        cookie.setMaxAge(60 * 60 * 1000);
//        cookie.setSecure(true) // Https 통신에서만 동작 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}
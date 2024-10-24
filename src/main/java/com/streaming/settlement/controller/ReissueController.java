package com.streaming.settlement.controller;

import com.streaming.settlement.dto.RefreshToken;
import com.streaming.settlement.jwt.JwtUtil;
import com.streaming.settlement.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static com.streaming.settlement.jwt.JwtUtil.*;

@Slf4j(topic = "JWT Access Token 재발급")
@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
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

        // Payload에 Refresh Token이 아닌 Access Token을 넣었을 경우 -> 에러 응답
        if (!tokenType.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // DB에 Refresh Token이 저장되어 있는지 확인 -> Refresh Token이 없을 경우 에러 응답
        Optional<RefreshToken> existRefreshToken = refreshTokenRepository
                .findByRefreshTokenAndUsername(username, authorization);
        if (existRefreshToken.isEmpty()) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        log.info("요청 유저 = username : {}, role : {}", username, role);

        // Access Token, Refresh Token 생성
        String accessToken = jwtUtil.generateToken("access", username, role, ACCESS_TOKEN_EXPIRY_TIME);
        String refreshToken = jwtUtil.generateToken("refresh", username, role, REFRESH_TOKEN_EXPIRY_TIME);

        // 새로 발급 받은 Refresh Token DB에 업데이트 (의존성 최소화를 위해 merge)
        RefreshToken newRefreshToken = RefreshToken.update(existRefreshToken.get(), refreshToken, REFRESH_TOKEN_EXPIRY_TIME);
        refreshTokenRepository.save(newRefreshToken);

        response.setHeader(AUTHORIZATION_HEADER, accessToken);
        response.addCookie(createCookie(refreshToken));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Cookie createCookie(String cookieValue) {
        // 토큰을 쿠키에 저장하기 위해 공백 encoding
//        cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue);
        cookie.setMaxAge(60 * 60 * 1000);
//        cookie.setSecure(true) // Https 통신에서만 동작 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}
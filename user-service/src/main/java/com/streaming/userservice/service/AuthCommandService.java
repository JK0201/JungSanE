package com.streaming.userservice.service;

import com.streaming.common.exception.InvalidTokenException;
import com.streaming.userservice.config.JwtUtil;
import com.streaming.userservice.dto.TokenWrapper;
import com.streaming.userservice.entity.RefreshToken;
import com.streaming.userservice.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.streaming.common.constant.JwtConstant.*;

@Slf4j(topic = "JWT Token 재발급")
@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.base.url}")
    private String REDIRECT_URL;

    /**
     * Cookie에서 추출한 기존 Refresh Token을 검증 후, Access Token과 Refresh Token 반환
     *
     * @param refreshToken (String)
     * @return TokenWrapper
     */
    @Transactional
    public TokenWrapper reissueToken(String refreshToken) {
        try {
            Claims claims = jwtUtil.validateAndExtractClaims(refreshToken);
            String userId = claims.get(CLAIM_USER_ID, String.class);
            String role = claims.get(CLAIM_ROLE, String.class);
            String tokenType = claims.get(GRANT_TYPE, String.class);

            // Grant_type (토큰 타입)이 Refresh Token이 아닐경우 에러 응답
            if (!tokenType.equals("REFRESH")) {
                log.error("Invalid token type: expected Refresh Token");
                throw new InvalidTokenException("Invalid token type: expected Refresh Token");
            }

            // DB에 Refresh Token이 저장되어 있는지 확인 -> Refresh Token이 없을 경우 에러 응답
            RefreshToken existingToken = refreshTokenRepository.findByUserIdAndRefreshToken(Long.parseLong(userId), refreshToken)
                    .orElseThrow(() -> new InvalidTokenException("Refresh token not found in database"));

            // Access Token, Refresh Token 생성
            String newAccessToken = jwtUtil.generateAccessToken(userId, role);
            String newRefreshToken = jwtUtil.generateRefreshToken(userId, role);

            // 새로 발급 받은 Refresh Token DB에 업데이트 (Dirty Checking)
            existingToken.update(newRefreshToken, jwtUtil.getExpiryTimeFromRefreshToken(refreshToken));

            // 새로 발급 받은 Access Token과 Refresh Token을 DTO로 반환
            return TokenWrapper.from(newAccessToken, newRefreshToken);
        } catch (Exception ex) {
            throw new InvalidTokenException("Token reissue failed");
        }
    }

    /**
     * Cookie에 Refresh Token이 있을 경우, DB의 Refresh Token 삭제
     * 연속적인 로그아웃 요청 방지를 위해 서비스 레이어에서 확인 후
     * 초기화 한 Refresh Token을 반환
     *
     * @param refreshToken (String)
     * @param response     (HttpServletResponse)
     */
    @Transactional
    public void logout(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.trim().isEmpty()) {
            // DB에서 Refresh Token 제거
            refreshTokenRepository.deleteByRefreshToken(refreshToken);

            // Refresh Token 쿠키 "정상화 신 창 섭"
            ResponseCookie cookie = jwtUtil.createCookie("", 0L);
            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }
    }
}

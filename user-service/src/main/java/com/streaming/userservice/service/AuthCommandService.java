package com.streaming.userservice.service;

import com.streaming.common.exception.InvalidTokenException;
import com.streaming.userservice.config.JwtUtil;
import com.streaming.userservice.dto.TokenWrapper;
import com.streaming.userservice.entity.RefreshToken;
import com.streaming.userservice.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.streaming.common.constant.JwtConstant.*;

@Slf4j(topic = "JWT Token 재발급")
@Service
@RequiredArgsConstructor
public class AuthCommandService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

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
}

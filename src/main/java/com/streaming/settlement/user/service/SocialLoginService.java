package com.streaming.settlement.user.service;

import com.streaming.settlement.common.config.JwtProvider;
import com.streaming.settlement.user.domain.User;
import com.streaming.settlement.user.infrastructure.kakao.KakaoUserInfoResponse;
import com.streaming.settlement.user.service.port.KakaoLogin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j(topic = "소셜 로그인 서비스")
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final KakaoLogin kakaoLogin;
    private final JwtProvider jwtProvider;

    /**
     * 카카오 소셜 로그인
     *
     * @param code (String)
     * @return JWT Token ("Bearer " + "Token)
     */
    public String kakaoLogin(String code) {
        String accessToken = kakaoLogin.getToken(code);
        KakaoUserInfoResponse currentUserInfo = kakaoLogin.getUserInfo(accessToken);
        User user = kakaoLogin.identifyUser(currentUserInfo);
        log.info("카카오 유저 로그인 = email : {}, last_login_at : {}", user.getEmail(), LocalDateTime.now());
        
        return jwtProvider.generateToken(user.getEmail(), user.getRole());
    }
}

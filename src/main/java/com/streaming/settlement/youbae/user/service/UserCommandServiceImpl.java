//package com.streaming.settlement.user.service;
//
//import com.streaming.settlement.user.controller.port.UserCommandService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class UserCommandServiceImpl implements UserCommandService {
//
//    private final SocialLoginService socialLoginService;
//
//    /**
//     * 카카오 소셜 로그인
//     *
//     * @param code (String)
//     * @return JWT Token ("Bearer " + "Token)
//     */
//    @Override
//    @Transactional
//    public String kakaoLogin(String code) {
//        return socialLoginService.kakaoLogin(code);
//    }
//}

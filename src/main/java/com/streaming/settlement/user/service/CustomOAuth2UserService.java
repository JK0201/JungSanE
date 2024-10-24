package com.streaming.settlement.user.service;

import com.streaming.settlement.user.dto.*;
import com.streaming.settlement.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j(topic = "OAuth2 유저 정보")
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 응답 받은 유저정보를 oAuth2Response 객체에 Provider에 따라서 Response 객체로 저장
        // 각 Provider에 따라 제공하는 값이 틀리기 때문에 switch-case로 작성
        OAuth2Response oAuth2Response;
        switch (registrationId) {
            case "google" -> oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
            case "naver" -> oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
            default -> {
                return null;
            }
        }

        log.info("User info = provider : {}, provider_id : {}, email : {}",
                oAuth2Response.getProvider(), oAuth2Response.getProviderId(), oAuth2Response.getEmail());

        // Authentication Provider에 넘겨줘야 로그인 진행됨
        // 사용자 고유 아이디값 생성 (Provider + ProviderId)
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();
        Optional<User> existUser = userRepository.findByUsername(username);

        // 최초 로그인이면 회원가입 후 로그인
        if (existUser.isEmpty()) {
            User user = User.fromOAuth(username, oAuth2Response);
            user = userRepository.save(user);
            return new CustomOAuth2User(user);
        }

        // 이미 가입한 유저라면 Authentication Provider에 해당 유저를 return
        return new CustomOAuth2User(existUser.get());
    }
}

package com.streaming.userservice.service;

import com.streaming.userservice.dto.GoogleResponse;
import com.streaming.userservice.dto.NaverResponse;
import com.streaming.userservice.dto.OAuth2Response;
import com.streaming.userservice.entity.AuthProvider;
import com.streaming.userservice.entity.User;
import com.streaming.userservice.repository.UserRepository;
import com.streaming.userservice.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j(topic = "OAuth2 유저 정보")
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response oAuth2Response = extractOAuth2Response(registrationId, oAuth2User.getAttributes());

        log.info("User info = provider : {}, provider_id : {}, email : {}",
                oAuth2Response.getProvider(), oAuth2Response.getProviderId(), oAuth2Response.getEmail());

        User user = findOrCreateUser(oAuth2Response);
        // Authentication Provider에 넘겨줘야 로그인 진행됨
        return new CustomOAuth2User(user);
    }

    /**
     * 응답 받은 유저정보를 oAuth2Response 객체에 Provider에 따라서 Response 객체로 저장
     * 각 Provider에 따라 제공하는 값이 틀리기 때문에 switch-case로 매핑
     *
     * @param registrationId Provider Id (String)
     * @param attributes     (OAuth2User)
     * @return OAuth2Response
     */
    private OAuth2Response extractOAuth2Response(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId) {
            case "google" -> new GoogleResponse(attributes);
            case "naver" -> new NaverResponse(attributes);
            default -> throw new IllegalArgumentException("잘못된 소셜 로그인 제공자 입니다.");
        };
    }

    /**
     * 최초 로그인이면 회원가입 후 로그인
     * 이미 가입한 유저라면 해당 유저를 return
     *
     * @param oAuth2Response (OAuth2Response)
     * @return User
     */
    private User findOrCreateUser(OAuth2Response oAuth2Response) {
        AuthProvider authProvider = AuthProvider.fromProvider(oAuth2Response.getProvider());
        // 사용자 고유 아이디 생성 (Provider + ProviderId)
        String username = oAuth2Response.getProvider() + "_" + oAuth2Response.getProviderId();

        return userRepository.findByProviderAndUsername(authProvider, username)
                .orElseGet(() -> {
                    User user = User.fromOAuth(username, oAuth2Response, authProvider);
                    return userRepository.save(user);
                });
    }
}

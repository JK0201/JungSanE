package com.streaming.userservice.oauth2;

import com.streaming.userservice.dto.response.GoogleResponse;
import com.streaming.userservice.dto.response.NaverResponse;
import com.streaming.userservice.dto.response.OAuth2Response;
import com.streaming.userservice.entity.user.AuthProvider;
import com.streaming.userservice.entity.user.User;
import com.streaming.userservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2 인증 성공 후, 사용자 정보를 로드하고 처리
     * 신규 회원은 등록하고, 기존 회원은 조회하여 인증 정보 반환
     *
     * @param userRequest Provider로 부터 받은 사용자 인증 정보 (OAuth2UserRequest)
     * @return CustomOAuth2User (사용자 인증 정보)
     */
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
                    User user = User.from(username, oAuth2Response, authProvider);
                    return userRepository.save(user);
                });
    }
}

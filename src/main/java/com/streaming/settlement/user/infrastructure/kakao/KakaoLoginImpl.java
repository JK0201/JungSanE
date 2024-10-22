package com.streaming.settlement.user.infrastructure.kakao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.streaming.settlement.user.domain.User;
import com.streaming.settlement.user.domain.UserType;
import com.streaming.settlement.user.service.port.KakaoLogin;
import com.streaming.settlement.user.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@Slf4j(topic = "카카오 로그인")
@Component
@RequiredArgsConstructor
public class KakaoLoginImpl implements KakaoLogin {

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;

    /**
     * 1. 인가 코드로 엑세스 토큰 요청
     *
     * @param code (String)
     * @return accessToken (String)
     */
    @Override
    public String getToken(String code) {
        log.info("인가 코드 = {}", code);

        // 엑세스 토큰 요청 URL 생성
        URI uri = UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com")
                .path("/oauth/token")
                .encode()
                .build()
                .toUri();

        // 엑세스 토큰 요청 Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

        // 엑세스 토큰 요청 Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoClientId);
        body.add("redirect_uri", baseUrl + "/api/v1/user/auth/kakao");
        body.add("code", code);

        // 엑세스 토큰 POST 요청 생성
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(body);

        // HTTP 요청 보내기 및 응답 받기
        ResponseEntity<KakaoAuthorizationResponse> response = restTemplate
                .exchange(requestEntity, KakaoAuthorizationResponse.class);

        return response.getBody().getAccessToken();
    }

    /**
     * 2. 발급 받은 Access Token을 사용하여 유저 정보 요청
     *
     * @param accessToken (String)
     * @return KakaoUserInfoResponse
     */
    @Override
    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        log.info("Access Token = {}", accessToken);

        // 사용자 정보 요청 URL 생성
        URI uri = UriComponentsBuilder
                .fromUriString("https://kapi.kakao.com")
                .path("/v2/user/me")
                .encode()
                .build()
                .toUri();

        // 사용자 정보 요청 Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 엑세스 토큰 POST 요청 생성 (보내줄 body 없음)
        RequestEntity<MultiValueMap<String, String>> requestEntity = RequestEntity
                .post(uri)
                .headers(headers)
                .body(new LinkedMultiValueMap<>());

        // HTTP 요청 보내기 및 응답 받기
        ResponseEntity<String> response = restTemplate
                .exchange(requestEntity, String.class);

        // JSON 파싱 및 DTO 반환
        try {
            JsonNode jsonNode = new ObjectMapper().readTree(response.getBody());
            String username = jsonNode.get("properties")
                    .get("nickname").asText();
            String email = jsonNode.get("kakao_account")
                    .get("email").asText();
            log.info("사용자 정보 = username : {}, email : {}", username, email);

            return KakaoUserInfoResponse.builder()
                    .username(username)
                    .email(email)
                    .userType(UserType.KAKAO)
                    .build();
        } catch (JsonProcessingException ex) {
            log.error("JSON 처리 중 오류 발생");
            // 추후 에러 처리 할 것
            throw new RuntimeException(ex);
        }
    }

    /**
     * 3. 유저 정보를 토대로 신규/기존 유저인지 판별
     * - 신규 유저일 경우 DB에 저장 -> 유저 정보 return
     * - 기존 유저일 경우 -> 해당 유저 정보 return
     *
     * @param currentUserInfo (User)
     */
    @Override
    public User identifyUser(KakaoUserInfoResponse currentUserInfo) {
        String email = currentUserInfo.getEmail();
        UserType userType = currentUserInfo.getUserType();
        Optional<User> user = userRepository.findByEmailAndUserType(email, userType);

        if (user.isEmpty()) {
            User newUser = User.from(currentUserInfo);
            log.info("신규 카카오 유저 = username : {}, email : {}", newUser.getUsername(), newUser.getEmail());

            return userRepository.save(newUser);
        }

        log.info("기존 카카오 유저 = username : {}, email : {}", user.get().getUsername(), user.get().getEmail());
        return user.get();
    }
}

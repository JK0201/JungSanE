package com.streaming.userservice.config;

import com.streaming.userservice.security.OAuth2SuccessHandler;
import com.streaming.userservice.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuth2UserService OAuth2UserService;
    private final OAuth2SuccessHandler OAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 (JWT 사용)
        http.csrf(AbstractHttpConfigurer::disable);

        // Form 로그인 방식 (disable)
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 방식 (disable)
        http.httpBasic(AbstractHttpConfigurer::disable);

        // Session 방식 (disable)
        http.sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Oauth2 (Custom한 OAuth2UserService를 엔드포인트로 설정)
        // customSuccessHandler를 등록하여 로그인 성공시 토큰 발급
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(OAuth2UserService))
                .successHandler(OAuth2SuccessHandler));

        // 경로 인가 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }
}

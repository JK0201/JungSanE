package com.streaming.settlement.user.config;

import com.streaming.settlement.user.jwt.CustomLogoutFilter;
import com.streaming.settlement.user.jwt.JwtAuthorizationFilter;
import com.streaming.settlement.user.jwt.JwtUtil;
import com.streaming.settlement.user.oauth2.CustomSuccessHandler;
import com.streaming.settlement.user.repository.RefreshTokenRepository;
import com.streaming.settlement.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정 (JWT 사용)
        http.csrf(AbstractHttpConfigurer::disable);

        // Form 로그인 방식 (disable)
        http.formLogin(AbstractHttpConfigurer::disable);

        // HTTP Basic 인증 방식 (disable)
        http.httpBasic(AbstractHttpConfigurer::disable);

        // Oauth2 (Custom한 OAuth2UserService를 엔드포인트로 설정)
        // customSuccessHandler를 등록하여 로그인 성공시 토큰 발급
        http.oauth2Login((oauth2) -> oauth2
                .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                        .userService(customOAuth2UserService))
                .successHandler(customSuccessHandler));

        // 경로 인가 설정
        http.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                .requestMatchers("/").permitAll()
                .requestMatchers("/reissue").permitAll()
                .anyRequest().authenticated());

        // Session 방식 (disable)
        http.sessionManagement((sessionManagement) -> sessionManagement
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // JwtFilter 추가 (
        http.addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);

        return http.build();
    }
}

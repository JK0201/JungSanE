package com.streaming.userservice.security;

import com.streaming.common.exception.ResourceNotFoundException;
import com.streaming.userservice.config.JwtUtil;
import com.streaming.userservice.entity.AuthProvider;
import com.streaming.userservice.entity.RefreshToken;
import com.streaming.userservice.entity.User;
import com.streaming.userservice.repository.RefreshTokenRepository;
import com.streaming.userservice.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

import static com.streaming.userservice.config.JwtUtil.AUTHORIZATION_HEADER;
import static com.streaming.userservice.config.JwtUtil.REFRESH_TOKEN_EXPIRY_TIME;

@Slf4j(topic = "로그인 인증 성공")
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.base.url}")
    private String BASE_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 성공적으로 Provider 사이트에 로그인한 유저의 정보가 넘어온 것을 사용하여 토큰 생성
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        // customUserDetails에서 username, UserRole, AuthProvider 가져오기
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        AuthProvider authProvider = customUserDetails.getAuthProvider();

        // Refresh Token 생성 (authProvider는 String으로 저장)
        String refreshToken = jwtUtil.generateToken("refresh", username, role, authProvider.getProvider(), REFRESH_TOKEN_EXPIRY_TIME);

        // Refresh Token DB 저장
        User user = userRepository.findByAuthProviderAndUsername(authProvider, username)
                .orElseThrow(() -> new ResourceNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username));
        RefreshToken newRefreshToken = RefreshToken
                .fromCreatedToken(refreshToken, REFRESH_TOKEN_EXPIRY_TIME, user);
        refreshTokenRepository.save(newRefreshToken);

        log.info("Last Login At = {}", LocalDateTime.now());

        response.addCookie(createCookie(refreshToken));
        response.sendRedirect(BASE_URL);
    }

    private Cookie createCookie(String cookieValue) {
        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue);
        cookie.setMaxAge(60 * 60 * 24 * 1000);
//        cookie.setSecure(true) // Https 통신에서만 동작 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}

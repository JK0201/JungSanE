package com.streaming.userservice.oauth2;

import com.streaming.userservice.config.security.JwtUtil;
import com.streaming.userservice.entity.RefreshToken;
import com.streaming.userservice.service.port.RefreshTokenRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

import static com.streaming.userservice.config.security.JwtUtil.REFRESH_TOKEN_EXPIRY_TIME;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.base.url}")
    private String REDIRECT_URL;

    /**
     * 소셜 로그인 인증 성공 후, Refresh Token을 생성하고 관리
     * 1. Refresh Token을 생성하여 DB에 저장
     * 2. 생성된 Token을 Cookie에 저장
     * 3. Gateway Service로 반환
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 성공적으로 Provider 사이트에 로그인한 유저의 정보가 넘어온 것을 사용하여 토큰 생성
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // customUserDetails에서 username, UserRole 가져오기
        String userId = oAuth2User.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // Refresh Token 생성 및 DB 저장
        String refreshToken = jwtUtil.generateRefreshToken(userId, role);
        saveRefreshToken(refreshToken, Long.parseLong(userId));

        log.info("Last Login At = {}", LocalDateTime.now());

        // Refresh Token 생성 및 Redirect 설정
        ResponseCookie cookie = jwtUtil.createCookie(refreshToken, REFRESH_TOKEN_EXPIRY_TIME);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect(REDIRECT_URL);
    }

    /**
     * Refresh Token DB 저장
     *
     * @param refreshToken (String)
     */
    private void saveRefreshToken(String refreshToken, Long userId) {
        RefreshToken newRefreshToken = RefreshToken.fromCreatedToken(
                refreshToken,
                jwtUtil.getExpiryTimeFromRefreshToken(refreshToken),
                userId);

        refreshTokenRepository.save(newRefreshToken);
    }
}

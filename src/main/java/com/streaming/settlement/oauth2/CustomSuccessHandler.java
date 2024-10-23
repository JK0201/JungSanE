package com.streaming.settlement.oauth2;

import com.streaming.settlement.dto.CustomOAuth2User;
import com.streaming.settlement.jwt.JwtUtil;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

import static com.streaming.settlement.jwt.JwtUtil.AUTHORIZATION_HEADER;

@Slf4j(topic = "로그인 인증 성공")
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;

    @Value("${spring.base.url}")
    private String BASE_URL;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 성공적으로 Provider 사이트에 로그인한 유저의 정보가 넘어온 것을 사용하여 토큰 생성
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();

        String username = customUserDetails.getUsername();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 토큰 생성
        String token = jwtUtil.generateToken(username, role, 60 * 60 * 60L);

        log.info("Last Login At = {}", LocalDateTime.now());

        // 쿠키로 구워서 클라이언트 리다이렉트
        response.addCookie(createCookie(token));
        response.sendRedirect(BASE_URL);
    }

    private Cookie createCookie(String cookieValue) {
        // 토큰을 쿠키에 저장하기 위해 공백 encoding
        cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue);
        cookie.setMaxAge(60 * 60 * 60);
//        cookie.setSecure(true) // Https 통신에서만 쿠키 사용
        cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}

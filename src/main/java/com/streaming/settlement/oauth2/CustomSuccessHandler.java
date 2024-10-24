package com.streaming.settlement.oauth2;

import com.streaming.settlement.dto.CustomOAuth2User;
import com.streaming.settlement.dto.RefreshToken;
import com.streaming.settlement.dto.User;
import com.streaming.settlement.jwt.JwtUtil;
import com.streaming.settlement.repository.RefreshTokenRepository;
import com.streaming.settlement.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

import static com.streaming.settlement.jwt.JwtUtil.AUTHORIZATION_HEADER;
import static com.streaming.settlement.jwt.JwtUtil.REFRESH_TOKEN_EXPIRY_TIME;

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

        // customUserDetails에서 username, role 가져오기
        String username = customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // Refresh Token 생성
        System.out.println("여기보셈" + LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRY_TIME / 1000));
        String refreshToken = jwtUtil.generateToken("refresh", username, role, REFRESH_TOKEN_EXPIRY_TIME);

        // Refresh Token DB 저장
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다. : " + username));
        RefreshToken newRefreshToken = RefreshToken
                .fromCreatedToken(refreshToken, REFRESH_TOKEN_EXPIRY_TIME, user);
        refreshTokenRepository.save(newRefreshToken);

        log.info("Last Login At = {}", LocalDateTime.now());

        response.addCookie(createCookie(refreshToken));
        response.sendRedirect(BASE_URL);
    }

    private Cookie createCookie(String cookieValue) {
        // 토큰을 쿠키에 저장하기 위해 공백 encoding
//        cookieValue = URLEncoder.encode(cookieValue, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, cookieValue);
        cookie.setMaxAge(60 * 60 * 24 * 1000);
//        cookie.setSecure(true) // Https 통신에서만 동작 설정
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}

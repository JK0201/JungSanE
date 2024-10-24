package com.streaming.settlement.user.jwt;

import com.streaming.settlement.user.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

import static com.streaming.settlement.user.jwt.JwtUtil.AUTHORIZATION_HEADER;

@Slf4j(topic = "로그아웃 필터")
@RequiredArgsConstructor
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // 로그아웃 URL + POST 요청 검증
        String requestUri = request.getRequestURI();
        String requestMethod = request.getMethod();

        if (!requestUri.equals("/logout") || !requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Refresh Token 쿠키에서 가져오기
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        // 토큰이 없을 경우 다음 필터 진행
        if (!StringUtils.hasText(refreshToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        log.error("여기임");

        // Refresh 토큰 삭제 및 쿠키 만료 처리
        refreshTokenRepository.deleteByRefreshToken(refreshToken);

        Cookie cookie = new Cookie(AUTHORIZATION_HEADER, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

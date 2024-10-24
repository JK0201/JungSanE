package com.streaming.settlement.jwt;

import com.streaming.settlement.dto.CustomOAuth2User;
import com.streaming.settlement.dto.User;
import com.streaming.settlement.entity.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.streaming.settlement.jwt.JwtUtil.*;

@Slf4j(topic = "JWT 인가 필터")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // "/api"로 들어오는 요청이 아닐 경우 다음 필터 진행
        if (!request.getRequestURI().startsWith("/api")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = jwtUtil.getAccessTokenFromHeader(request);

        log.info("요청 토큰 = {}", authorization);

        // 토큰이 없을 경우 -> 다음 필터로 진행
        if (!StringUtils.hasText(authorization)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 토큰이 유효하지 않은 경우 -> 에러 응답
        if (!jwtUtil.validateToken(authorization)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("유효하지 않은 토큰 입니다.");
            return;
        }

        // 토큰이 유요할 경우 토큰에서 유저 정보를 가져옴
        Claims userInfo = jwtUtil.userInformationFromToken(authorization);
        String tokenType = userInfo.get(GRANT_TYPE, String.class);
        String username = userInfo.get(CLAIM_USERNAME, String.class);
        String role = userInfo.get(CLAIM_ROLE, String.class);

        // Payload에 Access Token이 아닌 Refresh Token을 넣었을 경우 -> 에러 응답
        if (!tokenType.equals("access")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("유효하지 않은 토큰 입니다.");
            return;
        }

        log.info("요청 유저 = username : {}, role : {}", username, role);

        // User 객체에 담아서 커스텀한 OAuth2User에 넘겨줌
        User user = User.fromToken(username, UserRole.fromAuthority(role));
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(user);

        // 인증 처리 : Authentication 객체 생성 후, SecurityContextHolder에 인증 정보 저장
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}

package com.streaming.settlement.common.security.filter;

import com.streaming.settlement.common.config.JwtProvider;
import com.streaming.settlement.common.security.UserDetailsServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 토큰 검증 필터")
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request 헤더로 받은 토큰 검증을 위해 "Bearer " substring
        String tokenValue = jwtProvider.tokenFromHeader(request);

        // 토큰이 있을경우 조건문 내부 검증을 순차적으로 진행
        if (StringUtils.hasText(tokenValue)) {
            if (!jwtProvider.validateToken(tokenValue)) {
                log.error("Invalid JWT Token");
                return;
            }

            Claims userInfo = jwtProvider.userInformationFromToken(tokenValue);

            try {
                setAuthentication(userInfo.getSubject());
            } catch (Exception ex) {
                log.error("Invalid JWT Token = {}", ex.getMessage());
                return;
            }
        }

        // Spring 다음 필터 진행
        filterChain.doFilter(request, response);
    }

    // 인증 처리 : Authentication 객체 생성 후, SecurityContextHolder에 인증 정보 저장
    // 소셜 회원만 있는 상황이고, email이 Unique 필드
    public void setAuthentication(String email) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = createAuthentication(email);
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // Authentication 유저 인증 객체 생성
    private Authentication createAuthentication(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}

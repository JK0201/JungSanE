package com.streaming.settlement.user.controller;

import com.streaming.settlement.common.config.JwtProvider;
import com.streaming.settlement.user.controller.port.UserCommandService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserCommandController {

    @Value("${base.url}")
    private String baseUrl;

    private final UserCommandService userCommandService;
    
    /**
     * 카카오 로그인 콜백
     * 요청시 클라이언트에서 "Bearer "를 붙여서 요청 (소셜 로그인)
     *
     * @param code     (String)
     * @param response (HttpServletResponse)
     * @return 302 Redirect / Cookie (Authorization : Bearer를 제거한 Token, 클라이언트 요청시 헤더에 "Bearer " 추가)
     */
    @GetMapping("/v1/user/auth/kakao")
    public ResponseEntity<Void> kakaoCallback(@RequestParam("code") String code, HttpServletResponse response) {
        String token = userCommandService.kakaoLogin(code);

        Cookie cookie = new Cookie(JwtProvider.AUTHORIZATION_HEADER, token.substring(7));
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(baseUrl))
                .build();
    }
}

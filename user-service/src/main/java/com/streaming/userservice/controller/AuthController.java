package com.streaming.userservice.controller;

import com.streaming.userservice.config.JwtUtil;
import com.streaming.userservice.dto.TokenWrapper;
import com.streaming.userservice.service.AuthCommandService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.streaming.common.constant.JwtConstant.AUTHORIZATION_HEADER;
import static com.streaming.common.constant.JwtConstant.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v2")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final JwtUtil jwtUtil;

    @GetMapping("/test")
    public String mainAPI() {
        System.out.println("main");
        return "main route";
    }

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueToken(
            @CookieValue(name = REFRESH_TOKEN) String refreshToken,
            HttpServletResponse response
    ) {
        TokenWrapper tokens = authCommandService.reissueToken(refreshToken);

        // 새로운 Access Token Header에 추가
        response.setHeader(AUTHORIZATION_HEADER, tokens.getAccessToken());
        // 새로운 Refresh Token Cookie에 추가
        ResponseCookie cookie = jwtUtil.createCookie(refreshToken);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
package com.streaming.userservice.controller;

import com.streaming.userservice.config.security.JwtUtil;
import com.streaming.userservice.controller.port.AuthCommandService;
import com.streaming.userservice.dto.response.TokenWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.streaming.userservice.config.security.JwtUtil.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/v1")
public class AuthController {

    private final AuthCommandService authCommandService;
    private final JwtUtil jwtUtil;

    @PostMapping("/reissue")
    public ResponseEntity<Void> reissueToken(
            @CookieValue(name = REFRESH_TOKEN) String refreshToken,
            HttpServletResponse response
    ) {
        TokenWrapper tokens = authCommandService.reissueToken(refreshToken);
        // 새로운 Access Token Header에 추가
        response.setHeader(AUTHORIZATION_HEADER, tokens.getAccessToken());
        // 새로운 Refresh Token Cookie에 추가
        ResponseCookie cookie = jwtUtil.createCookie(tokens.getRefreshToken(), REFRESH_TOKEN_EXPIRY_TIME);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN, required = false) String refreshToken,
            HttpServletResponse response
    ) {
        authCommandService.logout(refreshToken, response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
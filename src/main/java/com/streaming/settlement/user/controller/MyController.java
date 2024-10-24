package com.streaming.settlement.user.controller;

import com.streaming.settlement.user.dto.CustomOAuth2User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    @GetMapping("/api/my")
    public ResponseEntity<String> myAPI(@AuthenticationPrincipal CustomOAuth2User detail) {
        return ResponseEntity
                .ok()
                .body(detail.getUsername() + detail.getAuthorities());
    }
}

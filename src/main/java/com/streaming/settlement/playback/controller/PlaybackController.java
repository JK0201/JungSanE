package com.streaming.settlement.playback.controller;

import com.streaming.settlement.playback.dto.PlaybackResponse;
import com.streaming.settlement.playback.service.PlaybackCommandService;
import com.streaming.settlement.user.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/playback")
public class PlaybackController {

    private final PlaybackCommandService playbackCommandService;

    @PostMapping("/{video_id}/start")
    public ResponseEntity<PlaybackResponse> start(
            @PathVariable("video_id") Long videoId,
            @AuthenticationPrincipal CustomOAuth2User oAuth2User
    ) {
        PlaybackResponse playbackResponse = playbackCommandService.start(videoId, oAuth2User);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playbackResponse);
    }

    @PostMapping("/{video_id}/stop")
    public ResponseEntity<PlaybackResponse> stop(
            @PathVariable("video_id") Long videoId,
            @AuthenticationPrincipal CustomOAuth2User user
    ) {
        PlaybackResponse playbackResponse = playbackCommandService.stop(videoId, user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playbackResponse);
    }
}

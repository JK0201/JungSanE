package com.streaming.settlement.playback.controller;

import com.streaming.settlement.playback.dto.Playback;
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
        Playback playback = playbackCommandService.start(videoId, oAuth2User);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(PlaybackResponse.from(playback));
    }

    @PostMapping("/{video_id}/stop")
    public ResponseEntity<Void> stop(
            @PathVariable("video_id") Long videoId,
            @AuthenticationPrincipal CustomOAuth2User user
    ) {
        Playback playback = playbackCommandService.stop(videoId, user);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}

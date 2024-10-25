package com.streaming.settlement.video.controller;

import com.streaming.settlement.user.security.CustomOAuth2User;
import com.streaming.settlement.video.dto.Video;
import com.streaming.settlement.video.dto.VideoPublish;
import com.streaming.settlement.video.dto.VideoResponse;
import com.streaming.settlement.video.service.VideoCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoCommandController {


    private final VideoCommandService videoCommandService;

    @PostMapping("/publish")
    public ResponseEntity<VideoResponse> publish(
            @Valid @RequestBody VideoPublish videoPublish,
            @AuthenticationPrincipal CustomOAuth2User authority) {
        Video video = videoCommandService.publish(videoPublish, authority);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(VideoResponse.from(video));
    }
}

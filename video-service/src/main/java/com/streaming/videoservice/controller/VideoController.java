package com.streaming.videoservice.controller;

import com.streaming.common.utils.RequestParser;
import com.streaming.videoservice.controller.port.PlaybackCommandService;
import com.streaming.videoservice.controller.port.VideoCommandService;
import com.streaming.videoservice.dto.request.VideoPublish;
import com.streaming.videoservice.dto.request.VideoStop;
import com.streaming.videoservice.dto.response.PlaybackResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoController {

    private final VideoCommandService videoCommandService;
    private final PlaybackCommandService playbackCommandService;

    @PostMapping("/publish")
    public ResponseEntity<Void> publish(
            HttpServletRequest request,
            @Valid @RequestBody VideoPublish videoPublish
    ) {
        Long userId = RequestParser.extractUserIdFromHeader(request);
        videoCommandService.publish(videoPublish, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @PostMapping("/{video_id}/start")
    public ResponseEntity<PlaybackResponse> start(
            HttpServletRequest request,
            @PathVariable("video_id") Long videoId
    ) {
        Long userId = RequestParser.extractUserIdFromHeader(request);
        PlaybackResponse playbackResponse = playbackCommandService.start(videoId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playbackResponse);
    }

    @PostMapping("/{video_id}/stop")
    public ResponseEntity<Void> stop(
            HttpServletRequest request,
            @PathVariable("video_id") Long videoId,
            @Valid @RequestBody VideoStop videoStop
    ) {
        Long userId = RequestParser.extractUserIdFromHeader(request);
        playbackCommandService.stop(videoId, videoStop, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}

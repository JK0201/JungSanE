package com.streaming.videoservice.controller;

import com.streaming.common.utils.RequestParser;
import com.streaming.videoservice.dto.PlaybackResponse;
import com.streaming.videoservice.dto.StopRequest;
import com.streaming.videoservice.dto.VideoPublish;
import com.streaming.videoservice.dto.VideoResponse;
import com.streaming.videoservice.service.VideoCommandService;
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

    @PostMapping("/publish")
    public ResponseEntity<VideoResponse> publish(
            HttpServletRequest request,
            @Valid @RequestBody VideoPublish videoPublish
    ) {
        Long userId = RequestParser.extractUserIdFromHeader(request);
        VideoResponse videoResponse = videoCommandService.publish(videoPublish, userId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(videoResponse);
    }

    @PostMapping("/{video_id}/start")
    public ResponseEntity<PlaybackResponse> start(
            HttpServletRequest request,
            @PathVariable("video_id") Long videoId
    ) {
        Long userId = RequestParser.extractUserIdFromHeader(request);
        PlaybackResponse playbackResponse = videoCommandService.start(videoId, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playbackResponse);
    }

    @PostMapping("/{video_id}/stop")
    public ResponseEntity<PlaybackResponse> stop(
            HttpServletRequest request,
            @PathVariable("video_id") Long videoId,
            @Valid @RequestBody StopRequest stopRequest
    ) {
        Long userId = RequestParser.extractUserIdFromHeader(request);
        PlaybackResponse playbackResponse = videoCommandService.stop(videoId, stopRequest, userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(playbackResponse);
    }
}

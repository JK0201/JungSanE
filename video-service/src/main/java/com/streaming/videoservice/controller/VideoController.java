package com.streaming.videoservice.controller;

import com.streaming.common.utils.RequestParser;
import com.streaming.videoservice.controller.port.VideoCommandService;
import com.streaming.videoservice.dto.request.VideoPublish;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/video")
public class VideoController {

    private final VideoCommandService videoCommandService;

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
}

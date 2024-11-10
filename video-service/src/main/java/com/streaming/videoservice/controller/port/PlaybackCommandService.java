package com.streaming.videoservice.controller.port;

import com.streaming.videoservice.dto.request.VideoStop;
import com.streaming.videoservice.dto.response.PlaybackResponse;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface PlaybackCommandService {

    PlaybackResponse start(Long videoId, Long userId);

    void stop(Long videoId, @Valid VideoStop videoStop, Long userId);
}

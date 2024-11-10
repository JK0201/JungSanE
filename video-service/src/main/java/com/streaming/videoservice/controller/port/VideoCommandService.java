package com.streaming.videoservice.controller.port;

import com.streaming.videoservice.dto.request.VideoPublish;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface VideoCommandService {

    void publish(@Valid VideoPublish videoPublish, Long userId);
}

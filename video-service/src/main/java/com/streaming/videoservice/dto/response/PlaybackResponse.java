package com.streaming.videoservice.dto.response;

import com.streaming.videoservice.entity.playback.Playback;
import com.streaming.videoservice.entity.playback.PlaybackStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlaybackResponse {

    private final Long id;
    private final Long userId;
    private final Long lastPlayPosition;
    private final PlaybackStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static PlaybackResponse of(Playback playback) {
        return PlaybackResponse.builder()
                .id(playback.getId())
                .userId(playback.getUserId())
                .lastPlayPosition(playback.getLastPlayPosition())
                .status(playback.getStatus())
                .createdAt(playback.getCreatedAt())
                .modifiedAt(playback.getModifiedAt())
                .build();
    }
}

package com.streaming.videoservice.dto;

import com.streaming.videoservice.entity.Playback;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlaybackResponse {

    private final Long id;
    private final Long lastPlayPosition;
    //    private final User user;
//    private final Video video;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static PlaybackResponse from(Playback playback) {
        return PlaybackResponse.builder()
                .id(playback.getId())
                .lastPlayPosition(playback.getLastPlayPosition())
//                .user(playback.getUser())
//                .video(playback.getVideo())
                .createdAt(playback.getCreatedAt())
                .modifiedAt(playback.getModifiedAt())
                .build();
    }
}

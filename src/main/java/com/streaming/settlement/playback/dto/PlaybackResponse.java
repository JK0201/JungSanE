package com.streaming.settlement.playback.dto;

import com.streaming.settlement.playback.entity.Playback;
import com.streaming.settlement.user.entity.User;
import com.streaming.settlement.video.entity.Video;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlaybackResponse {

    private Long id;
    private Long lastPlayTime;
    private User user;
    private Video video;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static PlaybackResponse from(Playback playback) {
        return PlaybackResponse.builder()
                .id(playback.getId())
                .lastPlayTime(playback.getLastPlayTime())
                .user(playback.getUser())
                .video(playback.getVideo())
                .createdAt(playback.getCreatedAt())
                .modifiedAt(playback.getModifiedAt())
                .build();
    }
}

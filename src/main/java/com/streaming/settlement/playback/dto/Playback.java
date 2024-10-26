package com.streaming.settlement.playback.dto;

import com.streaming.settlement.user.dto.User;
import com.streaming.settlement.video.dto.Video;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Playback {

    private final Long id;
    private final Long lastPlayTime;
    private final User user;
    private final Video video;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public Playback(Long id, Long lastPlayTime, User user, Video video, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.lastPlayTime = lastPlayTime;
        this.user = user;
        this.video = video;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Playback from(User user, Video video, Long lastPlayTime) {
        return Playback.builder()
                .lastPlayTime(lastPlayTime)
                .user(user)
                .video(video)
                .build();
    }
}

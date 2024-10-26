package com.streaming.settlement.video.dto;

import com.streaming.settlement.user.dto.User;
import com.streaming.settlement.video.entity.VideoStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Video {

    private final Long id;
    private final String title;
    private final String description;
    private final Long playbackTime;
    private final Long accumulatedViewCount;
    private final Long accumulatedPlaybackTime;
    private final VideoStatus status;
    private final User user;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    @Builder
    public Video(Long id, String title, String description, Long playbackTime, Long accumulatedViewCount, Long accumulatedPlaybackTime, VideoStatus status, User user, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.playbackTime = playbackTime;
        this.accumulatedViewCount = accumulatedViewCount;
        this.accumulatedPlaybackTime = accumulatedPlaybackTime;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
    }

    public static Video fromPublish(VideoPublish videoPublish, User user) {
        return Video.builder()
                .title(videoPublish.getTitle())
                .description(videoPublish.getDescription())
                .playbackTime(videoPublish.getPlaybackTime())
                .accumulatedViewCount(0L)
                .accumulatedPlaybackTime(0L)
                .status(VideoStatus.ACTIVE)
                .user(user)
                .build();
    }

    public static Video updateViewCount(Video video) {
        return Video.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .playbackTime(video.getPlaybackTime())
                .accumulatedViewCount(video.getAccumulatedViewCount() + 1)
                .accumulatedPlaybackTime(0L)
                .status(video.getStatus())
                .user(video.getUser())
                .build();
    }
}

package com.streaming.settlement.video.dto;

import com.streaming.settlement.user.entity.User;
import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.entity.VideoStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VideoResponse {

    private Long id;
    private String title;
    private String description;
    private Long playbackTime;
    private Long accumulatedViewCount;
    private Long accumulatedPlaybackTime;
    private VideoStatus status;
    private User user;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static VideoResponse from(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .playbackTime(video.getPlaybackTime())
                .accumulatedViewCount(video.getAccumulatedViewCount())
                .accumulatedPlaybackTime(video.getAccumulatedPlaybackTime())
                .status(video.getStatus())
                .user(video.getUser())
                .createdAt(video.getCreatedAt())
                .modifiedAt(video.getModifiedAt())
                .build();
    }
}

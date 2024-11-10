package com.streaming.videoservice.dto.response;

import com.streaming.videoservice.entity.Video;
import com.streaming.videoservice.entity.VideoStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VideoResponse {

    private final Long id;
    private final Long uploaderId;
    private final String title;
    private final String description;
    private final Long playbackTime;
    private final Long accumulatedViewCount;
    private final VideoStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static VideoResponse from(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .uploaderId(video.getUploaderId())
                .title(video.getTitle())
                .description(video.getDescription())
                .playbackTime(video.getPlaybackTime())
                .accumulatedViewCount(video.getAccumulatedViewCount())
                .status(video.getStatus())
                .createdAt(video.getCreatedAt())
                .modifiedAt(video.getModifiedAt())
                .build();
    }
}

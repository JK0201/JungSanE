package com.streaming.videoservice.dto;

import com.streaming.videoservice.entity.Video;
import com.streaming.videoservice.entity.VideoStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class VideoResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final Long playbackTime;
    private final Long accumulatedViewCount;
    private final Long accumulatedPlaybackTime;
    private final VideoStatus status;
    //    private final User user;
    //    private final List<Test> videoAdvertisementList;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static VideoResponse from(Video video) {
        return VideoResponse.builder()
                .id(video.getId())
                .title(video.getTitle())
                .description(video.getDescription())
                .playbackTime(video.getPlaybackTime())
                .accumulatedViewCount(video.getAccumulatedViewCount())
                .accumulatedPlaybackTime(video.getAccumulatedPlaybackTime())
                .status(video.getStatus())
//                .user(video.getUser())
//                .videoAdvertisementList(
//                        video.getVideoAdvertisementList()
//                                .stream()
//                                .map(Test::from)
//                                .toList())
                .createdAt(video.getCreatedAt())
                .modifiedAt(video.getModifiedAt())
                .build();
    }
}

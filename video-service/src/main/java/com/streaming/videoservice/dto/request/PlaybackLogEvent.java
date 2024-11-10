package com.streaming.videoservice.dto.request;

import com.streaming.videoservice.entity.PlaybackStatus;
import com.streaming.videoservice.entity.Video;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlaybackLogEvent {

    private final Long videoId;
    private final Long uploaderId;
    private final Long videoPlayedTime;
    private final Long advertisementViewCount;
    private final boolean isNewView;
    private final LocalDateTime createdAt;
    private final LocalDateTime modifiedAt;

    public static PlaybackLogEvent from(Video video, Long playedTime, Long advertisementViewCount, PlaybackStatus status) {
        return PlaybackLogEvent.builder()
                .videoId(video.getId())
                .uploaderId(video.getUploaderId())
                .videoPlayedTime(playedTime)
                .advertisementViewCount(advertisementViewCount)
                .isNewView(status == PlaybackStatus.PROGRESS)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}

package com.streaming.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PlaybackEvent {

    private final Long videoId;
    private final Long uploaderId;
    private final Long videoPlayedTime;
    private final Long advertisementViewCount;
    private final Boolean isNewView;
    private final LocalDateTime createdAt;

    public static PlaybackEvent from(Long videoId, Long uploaderId, Long playedTime, Long advertisementViewCount, Boolean status) {
        return PlaybackEvent.builder()
                .videoId(videoId)
                .uploaderId(uploaderId)
                .videoPlayedTime(playedTime)
                .advertisementViewCount(advertisementViewCount)
                .isNewView(status) // 추후 배치 작업시 : true -> 조회수 포함 / false -> 조회수 미포함
                .createdAt(LocalDateTime.now())
                .build();
    }
}

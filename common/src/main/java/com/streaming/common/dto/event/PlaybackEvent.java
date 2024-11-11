package com.streaming.common.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaybackEvent {

    private Long videoId;
    private Long uploaderId;
    private Long videoPlayedTime;
    private Long advertisementViewCount;
    private Boolean isNewView;
    private LocalDateTime createdAt;

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

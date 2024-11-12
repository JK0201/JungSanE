package com.streaming.adjustmentservice.dto;

import lombok.Getter;

@Getter
public class PlaybackSummary {

    private final Long videoPlayedTime;
    private final Long videoViewCount;
    private final Long advertisementViewCount;
    private final Long videoId;
    private final Long uploaderId;

    public PlaybackSummary(Long videoPlayedTime, Long videoViewCount, Long advertisementViewCount, Long videoId, Long uploaderId) {
        this.videoPlayedTime = videoPlayedTime;
        this.videoViewCount = videoViewCount;
        this.advertisementViewCount = advertisementViewCount;
        this.videoId = videoId;
        this.uploaderId = uploaderId;
    }
}

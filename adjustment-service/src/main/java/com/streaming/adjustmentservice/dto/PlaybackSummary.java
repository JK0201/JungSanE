package com.streaming.adjustmentservice.dto;

import lombok.Getter;

@Getter
public class PlaybackSummary {

    private final Long videoPlayedTime;
    private final Long videoViewCount;
    private final Long advertisementViewCount;
    private final Long videoId;

    public PlaybackSummary(Long videoPlayedTime, Long videoViewCount, Long advertisementViewCount, Long videoId) {
        this.videoPlayedTime = videoPlayedTime;
        this.videoViewCount = videoViewCount;
        this.advertisementViewCount = advertisementViewCount;
        this.videoId = videoId;
    }
}

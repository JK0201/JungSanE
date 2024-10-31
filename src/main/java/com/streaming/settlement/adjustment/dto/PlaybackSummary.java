package com.streaming.settlement.adjustment.dto;

import com.streaming.settlement.video.entity.Video;
import lombok.Getter;

@Getter
public class PlaybackSummary {

    private final Long videoPlayedTime;
    private final Long videoViewCount;
    private final Long advertisementViewCount;
    private final Video video;

    public PlaybackSummary(Long videoPlayedTime, Long videoViewCount, Long advertisementViewCount, Video video) {
        this.videoPlayedTime = videoPlayedTime;
        this.videoViewCount = videoViewCount;
        this.advertisementViewCount = advertisementViewCount;
        this.video = video;
    }
}

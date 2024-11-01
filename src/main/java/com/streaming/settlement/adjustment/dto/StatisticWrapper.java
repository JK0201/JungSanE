package com.streaming.settlement.adjustment.dto;

import com.streaming.settlement.adjustment.entity.DailyStatistic;
import com.streaming.settlement.video.entity.Video;
import com.streaming.settlement.video.entity.VideoSnapshot;
import lombok.Getter;

@Getter
public class StatisticWrapper {

    private final DailyStatistic dailyStatistic;
    private final VideoSnapshot videoSnapshot;
    private final Video video;

    public StatisticWrapper(DailyStatistic dailyStatistic, VideoSnapshot videoSnapshot, Video video) {
        this.dailyStatistic = dailyStatistic;
        this.videoSnapshot = videoSnapshot;
        this.video = video;
    }
}

package com.streaming.adjustmentservice.dto;

import com.streaming.adjustmentservice.entity.settlement.VideoSnapshot;
import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import lombok.Getter;

@Getter
public class StatisticWrapper {

    private final DailyStatistic dailyStatistic;
    private final VideoSnapshot videoSnapshot;

    public StatisticWrapper(DailyStatistic dailyStatistic, VideoSnapshot videoSnapshot) {
        this.dailyStatistic = dailyStatistic;
        this.videoSnapshot = videoSnapshot;
    }
}

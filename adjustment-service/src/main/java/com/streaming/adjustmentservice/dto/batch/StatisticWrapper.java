package com.streaming.adjustmentservice.dto.batch;

import com.streaming.adjustmentservice.entity.settlement.VideoSnapshot;
import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StatisticWrapper {

    private final DailyStatistic dailyStatistic;
    private final VideoSnapshot videoSnapshot;
    
    public static StatisticWrapper of(DailyStatistic dailyStatistic, VideoSnapshot videoSnapshot) {
        return StatisticWrapper.builder()
                .dailyStatistic(dailyStatistic)
                .videoSnapshot(videoSnapshot)
                .build();
    }
}

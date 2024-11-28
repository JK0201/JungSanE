package com.streaming.adjustmentservice.dto.response;

import com.streaming.adjustmentservice.dto.request.DateRange;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StatisticResponse {

    private final String period;
    private final String type;
    private final DateRange dateRange;
    private final List<Top5VideosWrapper> top5Videos;

    public static StatisticResponse of(String period, String type, DateRange dateRange, List<Top5VideosWrapper> top5VideoList) {
        return StatisticResponse.builder()
                .period(period)
                .type(type)
                .dateRange(dateRange)
                .top5Videos(top5VideoList)
                .build();
    }
}

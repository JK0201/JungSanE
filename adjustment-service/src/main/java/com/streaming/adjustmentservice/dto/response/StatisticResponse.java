package com.streaming.adjustmentservice.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.streaming.adjustmentservice.dto.request.DateRange;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@JsonDeserialize(builder = StatisticResponse.StatisticResponseBuilder.class)
public class StatisticResponse {

    private final String type;
    private final DateRange dateRange;
    private final List<TopVideosResponse> top5Videos;

    @JsonPOJOBuilder(withPrefix = "")
    public static class StatisticResponseBuilder {
    }

    public static StatisticResponse of(String type, DateRange dateRange, List<TopVideosResponse> topVideoList) {
        return StatisticResponse.builder()
                .type(type)
                .dateRange(dateRange)
                .top5Videos(topVideoList)
                .build();
    }
}

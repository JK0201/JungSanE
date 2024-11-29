package com.streaming.adjustmentservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.streaming.adjustmentservice.entity.statistic.PeriodType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@JsonDeserialize(builder = DateRange.DateRangeBuilder.class)
public class DateRange {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private final LocalDate endDate;

    private final PeriodType periodType;

    @JsonPOJOBuilder(withPrefix = "")
    public static class DateRangeBuilder {
    }

    public static DateRange of(LocalDate startDate, LocalDate endDate, PeriodType periodType) {
        return DateRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .periodType(periodType)
                .build();
    }
}

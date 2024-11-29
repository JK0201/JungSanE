package com.streaming.adjustmentservice.dto.request;

import com.streaming.adjustmentservice.entity.statistic.PeriodType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DateRange {

    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PeriodType periodType;

    public static DateRange of(LocalDate startDate, LocalDate endDate, PeriodType periodType) {
        return DateRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .periodType(periodType)
                .build();
    }
}

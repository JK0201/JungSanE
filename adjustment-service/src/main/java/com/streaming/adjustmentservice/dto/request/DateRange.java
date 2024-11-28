package com.streaming.adjustmentservice.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class DateRange {
    
    private final LocalDate startDate;
    private final LocalDate endDate;

    public static DateRange of(LocalDate startDate, LocalDate endDate) {
        return DateRange.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}

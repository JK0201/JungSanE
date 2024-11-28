package com.streaming.adjustmentservice.dto.response;

import com.streaming.adjustmentservice.dto.request.DateRange;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementResponse {

    private final String period;
    private final DateRange dateRange;
    private final Long userId;

    public static SettlementResponse of(String period, DateRange dateRange, Long userId) {
        return SettlementResponse.builder()
                .period(period)
                .dateRange(dateRange)
                .userId(userId)
                .build();
    }
}

package com.streaming.adjustmentservice.dto.response;

import com.streaming.adjustmentservice.dto.request.DateRange;
import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SettlementResponse {

    private final DateRange dateRange;
    private final Long userId;
    private final List<DailySettlement> dailySettlement;

    public static SettlementResponse of(DateRange dateRange, Long userId, List<DailySettlement> dailySettlement) {
        return SettlementResponse.builder()
                .dateRange(dateRange)
                .userId(userId)
                .dailySettlement(dailySettlement)
                .build();
    }
}

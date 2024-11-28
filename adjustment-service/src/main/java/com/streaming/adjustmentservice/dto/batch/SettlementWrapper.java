package com.streaming.adjustmentservice.dto.batch;

import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import com.streaming.adjustmentservice.entity.settlement.VideoSnapshot;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementWrapper {

    private final DailySettlement dailySettlement;
    private final VideoSnapshot videoSnapshot;

    public static SettlementWrapper of(DailySettlement dailySettlement, VideoSnapshot videoSnapshot) {
        return SettlementWrapper.builder()
                .dailySettlement(dailySettlement)
                .videoSnapshot(videoSnapshot)
                .build();
    }
}

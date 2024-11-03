package com.streaming.adjustmentservice.dto;

import com.streaming.adjustmentservice.entity.DailySettlement;
import com.streaming.adjustmentservice.entity.VideoSnapshot;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettlementWrapper {

    private final DailySettlement dailySettlement;
    private final VideoSnapshot videoSnapshot;

    public static SettlementWrapper from(DailySettlement dailySettlement, VideoSnapshot videoSnapshot) {
        return SettlementWrapper.builder()
                .dailySettlement(dailySettlement)
                .videoSnapshot(videoSnapshot)
                .build();
    }
}

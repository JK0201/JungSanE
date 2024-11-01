package com.streaming.settlement.adjustment.dto;

import com.streaming.settlement.adjustment.entity.DailySettlement;
import com.streaming.settlement.video.entity.VideoSnapshot;
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

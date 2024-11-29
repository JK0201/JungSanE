package com.streaming.adjustmentservice.controller.port;

import com.streaming.adjustmentservice.dto.response.SettlementResponse;
import com.streaming.adjustmentservice.dto.response.StatisticResponse;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AdjustmentQueryService {

    StatisticResponse getTop5Videos(String period, String type);

    SettlementResponse getSettlements(Long uploaderId, String period);
}

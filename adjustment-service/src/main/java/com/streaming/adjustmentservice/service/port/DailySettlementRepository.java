package com.streaming.adjustmentservice.service.port;

import com.streaming.adjustmentservice.entity.settlement.DailySettlement;

import java.time.LocalDate;
import java.util.List;

public interface DailySettlementRepository {

    List<DailySettlement> findVideoSettlements(Long uploaderId, LocalDate startDate, LocalDate endDate);
}

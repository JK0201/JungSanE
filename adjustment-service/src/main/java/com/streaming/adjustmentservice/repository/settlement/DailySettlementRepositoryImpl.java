package com.streaming.adjustmentservice.repository.settlement;

import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import com.streaming.adjustmentservice.service.port.DailySettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailySettlementRepositoryImpl implements DailySettlementRepository {

    private final DailySettlementJpaRepository dailySettlementJpaRepository;

    @Override
    public List<DailySettlement> findVideoSettlements(Long uploaderId, LocalDate startDate, LocalDate endDate) {
        return dailySettlementJpaRepository.findVideoSettlements(uploaderId, startDate, endDate);
    }
}

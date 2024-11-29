package com.streaming.adjustmentservice.repository.settlement;

import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailySettlementJpaRepository extends JpaRepository<DailySettlement, Long> {
    
    List<DailySettlement> findVideoSettlements(Long uploaderId, LocalDate startDate, LocalDate endDate);
}

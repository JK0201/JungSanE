package com.streaming.adjustmentservice.repository.settlement;

import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailySettlementJpaRepository extends JpaRepository<DailySettlement, Long> {
}

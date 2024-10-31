package com.streaming.settlement.adjustment.repository;

import com.streaming.settlement.adjustment.entity.DailyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyStatisticJpaRepository extends JpaRepository<DailyStatistic, Long> {

}

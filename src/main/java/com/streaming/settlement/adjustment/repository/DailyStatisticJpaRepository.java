package com.streaming.settlement.adjustment.repository;

import com.streaming.settlement.adjustment.entity.DailyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DailyStatisticJpaRepository extends JpaRepository<DailyStatistic, Long> {

    @Query("select ds from DailyStatistic ds")
    void asdf();
}

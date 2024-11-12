package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatisticJpaRepository extends JpaRepository<DailyStatistic, Long> {

    @Query("select ds from DailyStatistic ds " +
            "where ds.statisticDate = :yesterday " +
            "order by ds.videoViewCount desc " +
            "limit 5")
    List<DailyStatistic> findDailyTop5ByVideoViewCount(@Param("yesterday") LocalDate yesterday);
}

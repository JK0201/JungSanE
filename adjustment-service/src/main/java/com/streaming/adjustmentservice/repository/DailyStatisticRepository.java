package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.DailyStatistic;
import org.springframework.batch.item.Chunk;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatisticRepository {

    void saveAll(Chunk<? extends DailyStatistic> statistics);

    List<DailyStatistic> findDailyTop5ByVideoViewCount(LocalDate yesterday);
}

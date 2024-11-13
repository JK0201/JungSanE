package com.streaming.adjustmentservice.service.port;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import org.springframework.batch.item.Chunk;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatisticRepository {

    void saveAll(Chunk<? extends DailyStatistic> statistics);

    List<DailyStatistic> findDailyTop5ByVideoViewCount(LocalDate yesterday);

    void save(DailyStatistic dailyStatistic);

    void findById(long l);

}

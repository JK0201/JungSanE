package com.streaming.settlement.adjustment.repository;

import com.streaming.settlement.adjustment.entity.DailyStatistic;
import org.springframework.batch.item.Chunk;

public interface DailyStatisticRepository {

    void saveAll(Chunk<? extends DailyStatistic> statistics);
}

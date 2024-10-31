package com.streaming.settlement.adjustment.repository;

import com.streaming.settlement.adjustment.entity.DailyStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DailyStatisticRepositoryImpl implements DailyStatisticRepository {

    private final DailyStatisticJpaRepository dailyStatisticJpaRepository;

    @Override
    public void saveAll(Chunk<? extends DailyStatistic> statistics) {
        dailyStatisticJpaRepository.saveAll(statistics);
    }
}

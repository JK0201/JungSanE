package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.DailyStatistic;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyStatisticRepositoryImpl implements DailyStatisticRepository {

    private final DailyStatisticJpaRepository dailyStatisticJpaRepository;

    @Override
    public void saveAll(Chunk<? extends DailyStatistic> statistics) {
        dailyStatisticJpaRepository.saveAll(statistics);
    }

    @Override
    public List<DailyStatistic> findDailyTop5ByVideoViewCount(LocalDate yesterday) {
        return dailyStatisticJpaRepository.findDailyTop5ByVideoViewCount(yesterday);
    }
}

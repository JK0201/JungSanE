package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import com.streaming.adjustmentservice.service.port.DailyStatisticRepository;
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

    @Override
    public void save(DailyStatistic dailyStatistic) {
        dailyStatisticJpaRepository.save(dailyStatistic);
    }

    @Override
    public void findById(long l) {
        dailyStatisticJpaRepository.findById(l);
    }

    @Override
    public void upsertStatistic(
            Long videoId,
            Long uploaderId,
            Long videoPlayedTime,
            Long videoViewCount,
            Long advertisementViewCount,
            LocalDate statisticDate
    ) {
        dailyStatisticJpaRepository.upsertStatistic(
                videoId,
                uploaderId,
                videoPlayedTime,
                videoViewCount,
                advertisementViewCount,
                statisticDate);
    }
}

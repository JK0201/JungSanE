package com.streaming.adjustmentservice.repository.statistic;

import com.streaming.adjustmentservice.dto.response.Top5VideosWrapper;
import com.streaming.adjustmentservice.service.port.DailyStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DailyStatisticRepositoryImpl implements DailyStatisticRepository {

    private final DailyStatisticJpaRepository dailyStatisticJpaRepository;

    @Override
    public List<Top5VideosWrapper> findDailyTop5VideosByViewCount(LocalDate now) {
        return dailyStatisticJpaRepository.findDailyTop5VideosByViewCount(now);
    }

    @Override
    public List<Top5VideosWrapper> findDailyTop5VideosByPlayedTime(LocalDate now) {
        return dailyStatisticJpaRepository.findDailyTop5VideosByPlayedTime(now);
    }

    @Override
    public List<Top5VideosWrapper> findRangeTop5VideosByViewCount(LocalDate startDate, LocalDate endDate) {
        return dailyStatisticJpaRepository.findRangeTop5VideosByViewCount(startDate, endDate);
    }

    @Override
    public List<Top5VideosWrapper> findRangeTop5VideosByPlayedTime(LocalDate startDate, LocalDate endDate) {
        return dailyStatisticJpaRepository.findRangeTop5VideosByPlayedTime(startDate, endDate);
    }
}

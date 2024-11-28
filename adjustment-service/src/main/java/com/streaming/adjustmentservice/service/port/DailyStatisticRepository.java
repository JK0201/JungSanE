package com.streaming.adjustmentservice.service.port;

import com.streaming.adjustmentservice.dto.response.Top5VideosWrapper;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatisticRepository {

    List<Top5VideosWrapper> findDailyTop5VideosByViewCount(LocalDate now);

    List<Top5VideosWrapper> findDailyTop5VideosByPlayedTime(LocalDate now);

    List<Top5VideosWrapper> findRangeTop5VideosByViewCount(LocalDate startDate, LocalDate endDate);

    List<Top5VideosWrapper> findRangeTop5VideosByPlayedTime(LocalDate startDate, LocalDate endDate);
    
}

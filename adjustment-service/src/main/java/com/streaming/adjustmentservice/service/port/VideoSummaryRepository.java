package com.streaming.adjustmentservice.service.port;

import com.streaming.adjustmentservice.dto.response.TopVideosResponse;
import com.streaming.adjustmentservice.entity.statistic.PeriodType;

import java.time.LocalDate;
import java.util.List;

public interface VideoSummaryRepository {
    
    List<TopVideosResponse> findTopViewedVideos(LocalDate startDate, LocalDate endDate, PeriodType periodType);

    List<TopVideosResponse> findTopPlayedVideos(LocalDate startDate, LocalDate endDate, PeriodType periodType);
}

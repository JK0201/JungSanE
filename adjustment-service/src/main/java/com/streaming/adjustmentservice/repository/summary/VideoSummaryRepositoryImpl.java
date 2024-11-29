package com.streaming.adjustmentservice.repository.summary;

import com.streaming.adjustmentservice.dto.response.TopVideosResponse;
import com.streaming.adjustmentservice.entity.statistic.PeriodType;
import com.streaming.adjustmentservice.service.port.VideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VideoSummaryRepositoryImpl implements VideoSummaryRepository {

    private final VideoSummaryJpaRepository videoSummaryJpaRepository;

    @Override
    public List<TopVideosResponse> findTopViewedVideos(LocalDate startDate, LocalDate endDate, PeriodType periodType) {
        return videoSummaryJpaRepository.findTopViewedVideos(startDate, endDate, periodType);
    }

    @Override
    public List<TopVideosResponse> findTopPlayedVideos(LocalDate startDate, LocalDate endDate, PeriodType periodType) {
        return videoSummaryJpaRepository.findTopPlayedVideos(startDate, endDate, periodType);
    }
}

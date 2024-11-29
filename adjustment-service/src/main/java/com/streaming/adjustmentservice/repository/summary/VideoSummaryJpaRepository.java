package com.streaming.adjustmentservice.repository.summary;

import com.streaming.adjustmentservice.dto.response.TopVideosResponse;
import com.streaming.adjustmentservice.entity.statistic.PeriodType;
import com.streaming.adjustmentservice.entity.statistic.VideoSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface VideoSummaryJpaRepository extends JpaRepository<VideoSummary, Long> {

    @Query("SELECT new com.streaming.adjustmentservice.dto.response.TopVideosResponse(vs.videoId, vs.uploaderId, vs.videoViewCount) " +
            "FROM VideoSummary vs " +
            "WHERE vs.periodType = :periodType " +
            "AND vs.startDate >= :startDate " +
            "AND vs.endDate <= :endDate " +
            "ORDER BY vs.videoViewCount DESC " +
            "LIMIT 5")
    List<TopVideosResponse> findTopViewedVideos(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("periodType") PeriodType periodType);

    @Query("SELECT new com.streaming.adjustmentservice.dto.response.TopVideosResponse(vs.videoId, vs.uploaderId, vs.videoPlayedTime) " +
            "FROM VideoSummary vs " +
            "WHERE vs.periodType = :periodType " +
            "AND vs.startDate >= :startDate " +
            "AND vs.endDate <= :endDate " +
            "ORDER BY vs.videoPlayedTime DESC " +
            "LIMIT 5")
    List<TopVideosResponse> findTopPlayedVideos(LocalDate startDate, LocalDate endDate, PeriodType periodType);
}

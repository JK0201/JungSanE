package com.streaming.adjustmentservice.repository.statistic;

import com.streaming.adjustmentservice.dto.response.Top5VideosWrapper;
import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatisticJpaRepository extends JpaRepository<DailyStatistic, Long> {

    @Query("SELECT new com.streaming.adjustmentservice.dto.response.Top5VideosWrapper(ds.videoId, ds.uploaderId, ds.videoViewCount) " +
            "FROM DailyStatistic ds " +
            "WHERE ds.statisticDate = :now " +
            "ORDER BY ds.videoViewCount DESC " +
            "LIMIT 5")
    List<Top5VideosWrapper> findDailyTop5VideosByViewCount(@Param("now") LocalDate now);

    @Query("SELECT new com.streaming.adjustmentservice.dto.response.Top5VideosWrapper(ds.videoId, ds.uploaderId, ds.videoPlayedTime) " +
            "FROM DailyStatistic ds " +
            "WHERE ds.statisticDate = :now " +
            "ORDER BY ds.videoPlayedTime DESC " +
            "LIMIT 5")
    List<Top5VideosWrapper> findDailyTop5VideosByPlayedTime(@Param("now") LocalDate now);

    @Query("SELECT new com.streaming.adjustmentservice.dto.response.Top5VideosWrapper(ds.videoId, ds.uploaderId, SUM(ds.videoViewCount)) " +
            "FROM DailyStatistic ds " +
            "WHERE ds.statisticDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ds.videoId, ds.uploaderId " +
            "ORDER BY SUM(ds.videoViewCount) DESC " +
            "LIMIT 5")
    List<Top5VideosWrapper> findRangeTop5VideosByViewCount(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("SELECT new com.streaming.adjustmentservice.dto.response.Top5VideosWrapper(ds.videoId, ds.uploaderId, SUM(ds.videoPlayedTime)) " +
            "FROM DailyStatistic ds " +
            "WHERE ds.statisticDate BETWEEN :startDate AND :endDate " +
            "GROUP BY ds.videoId, ds.uploaderId " +
            "ORDER BY SUM(ds.videoPlayedTime) DESC " +
            "LIMIT 5")
    List<Top5VideosWrapper> findRangeTop5VideosByPlayedTime(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}

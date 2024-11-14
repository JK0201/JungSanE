package com.streaming.adjustmentservice.repository;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface DailyStatisticJpaRepository extends JpaRepository<DailyStatistic, Long> {

    @Query("select ds from DailyStatistic ds " +
            "where ds.statisticDate = :yesterday " +
            "order by ds.videoViewCount desc " +
            "limit 5")
    List<DailyStatistic> findDailyTop5ByVideoViewCount(@Param("yesterday") LocalDate yesterday);

    @Transactional
    @Modifying
    @Query(value = """
            INSERT INTO daily_statistics (video_id, uploader_id, video_played_time, video_view_count, advertisement_view_count, statistic_date) 
            VALUES (:videoId, :uploaderId, :videoPlayedTime, :videoViewCount, :advertisementViewCount, :statisticDate) 
            ON CONFLICT (video_id, uploader_id, statistic_date) DO UPDATE SET
                    video_played_time = daily_statistics.video_played_time + :videoPlayedTime,
                    video_view_count = daily_statistics.video_view_count + :videoViewCount,
                    advertisement_view_count = daily_statistics.advertisement_view_count + :advertisementViewCount
            """, nativeQuery = true)
    void upsertStatistic(
            @Param("videoId") Long videoId,
            @Param("uploaderId") Long uploaderId,
            @Param("videoPlayedTime") Long videoPlayedTime,
            @Param("videoViewCount") Long videoViewCount,
            @Param("advertisementViewCount") Long advertisementViewCount,
            @Param("statisticDate") LocalDate statisticDate
    );
}

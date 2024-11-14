package com.streaming.adjustmentservice.entity.statistic;

import com.streaming.adjustmentservice.dto.PlaybackSummary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "dailyStatistics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyStatistic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_statistic_id")
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private Long videoPlayedTime;

    @Column(nullable = false)
    private Long videoViewCount;

    @Column(nullable = false)
    private Long advertisementViewCount;

    @Column(nullable = false)
    private LocalDate statisticDate;

//    public static DailyStatistic fromPlaybackLog(PlaybackLog playbackLog) {
//        DailyStatistic dailyStatistic = new DailyStatistic();
//        dailyStatistic.videoPlayedTime = playbackLog.getVideoPlayedTime();
//        dailyStatistic.videoViewCount = playbackLog.getIsNewView() ? 1L : 0L;
//        dailyStatistic.advertisementViewCount = playbackLog.getAdvertisementViewCount();
//        dailyStatistic.statisticDate = playbackLog.getCreatedAt().toLocalDate();
//        dailyStatistic.videoId = playbackLog.getVideoId();
//        dailyStatistic.uploaderId = playbackLog.getUploaderId();
//        return dailyStatistic;
//    }

    public static DailyStatistic fromSummary(PlaybackSummary playbackSummary, LocalDateTime statisticDate) {
        DailyStatistic dailyStatistic = new DailyStatistic();
        dailyStatistic.videoPlayedTime = playbackSummary.getVideoPlayedTime();
        dailyStatistic.videoViewCount = playbackSummary.getVideoViewCount();
        dailyStatistic.advertisementViewCount = playbackSummary.getAdvertisementViewCount();
        dailyStatistic.statisticDate = statisticDate.toLocalDate();
        dailyStatistic.videoId = playbackSummary.getVideoId();
        dailyStatistic.uploaderId = playbackSummary.getUploaderId();
        return dailyStatistic;
    }
}

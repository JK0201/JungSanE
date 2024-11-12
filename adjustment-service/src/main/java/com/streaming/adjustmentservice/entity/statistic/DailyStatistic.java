package com.streaming.adjustmentservice.entity.statistic;

import com.streaming.adjustmentservice.dto.PlaybackSummary;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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

    public static DailyStatistic fromSummary(PlaybackSummary playbackSummary) {
        DailyStatistic dailyStatistic = new DailyStatistic();
        dailyStatistic.videoPlayedTime = playbackSummary.getVideoPlayedTime();
        dailyStatistic.videoViewCount = playbackSummary.getVideoViewCount();
        dailyStatistic.advertisementViewCount = playbackSummary.getAdvertisementViewCount();
        dailyStatistic.statisticDate = LocalDate.now().minusDays(1);
        dailyStatistic.videoId = playbackSummary.getVideoId();
        return dailyStatistic;
    }
}

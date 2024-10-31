package com.streaming.settlement.adjustment.entity;

import com.streaming.settlement.adjustment.dto.PlaybackSummary;
import com.streaming.settlement.video.entity.Video;
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
    private Long videoPlayedTime;

    @Column(nullable = false)
    private Long videoViewCount;

    @Column(nullable = false)
    private Long advertisementViewCount;

    @Column(nullable = false)
    private LocalDate statisticDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static DailyStatistic fromSummary(PlaybackSummary playbackSummary) {
        DailyStatistic dailyStatistic = new DailyStatistic();
        dailyStatistic.videoPlayedTime = playbackSummary.getVideoPlayedTime();
        dailyStatistic.videoViewCount = playbackSummary.getVideoViewCount();
        dailyStatistic.advertisementViewCount = playbackSummary.getAdvertisementViewCount();
        dailyStatistic.statisticDate = LocalDate.now().minusDays(1);
        dailyStatistic.video = playbackSummary.getVideo();
        return dailyStatistic;
    }
}

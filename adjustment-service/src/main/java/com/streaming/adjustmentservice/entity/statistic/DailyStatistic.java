package com.streaming.adjustmentservice.entity.statistic;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "daily_statistic",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_statistic_video_date",
                        columnNames = {"video_id", "statistic_date"}
                )
        }
)
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

    public static DailyStatistic of(long videoId, long uploaderId, long videoPlayedTime, long videoViewCount, long advertisementViewCount, LocalDate statisticDate) {
        DailyStatistic dailyStatistic = new DailyStatistic();
        dailyStatistic.videoId = videoId;
        dailyStatistic.uploaderId = uploaderId;
        dailyStatistic.videoPlayedTime = videoPlayedTime;
        dailyStatistic.videoViewCount = videoViewCount;
        dailyStatistic.advertisementViewCount = advertisementViewCount;
        dailyStatistic.statisticDate = statisticDate;
        return dailyStatistic;
    }

    public static DailyStatistic test(long videoId, long uploaderId, long videoPlayedTime, long videoViewCount, long advertisementViewCount, LocalDate yesterday) {
        DailyStatistic dailyStatistic = new DailyStatistic();
        dailyStatistic.videoId = videoId;
        dailyStatistic.uploaderId = uploaderId;
        dailyStatistic.videoPlayedTime = videoPlayedTime;
        dailyStatistic.videoViewCount = videoViewCount;
        dailyStatistic.advertisementViewCount = advertisementViewCount;
        dailyStatistic.statisticDate = yesterday;
        return dailyStatistic;
    }

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
}

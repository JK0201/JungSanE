package com.streaming.adjustmentservice.entity.statistic;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "daily_statistic",
        indexes = {
                @Index(
                        name = "idx_statistic_date_id",
                        columnList = "statistic_date, daily_statistic_id"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_id_date",
                        columnNames = {"video_id", "statistic_date"}
                )
        })
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
}

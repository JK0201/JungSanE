package com.streaming.adjustmentservice.entity.settlement;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "video_snapshot",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_id",
                        columnNames = {"video_id"}
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_snapshot_id")
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Long videoViewCount;

    @Column(nullable = false)
    private Long advertisementViewCount;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    public static VideoSnapshot of(long videoId, long videoViewCount, long advertisementViewCount, LocalDate snapshotDate) {
        VideoSnapshot videoSnapshot = new VideoSnapshot();
        videoSnapshot.videoViewCount = videoViewCount;
        videoSnapshot.advertisementViewCount = advertisementViewCount;
        videoSnapshot.snapshotDate = snapshotDate;
        videoSnapshot.videoId = videoId;
        return videoSnapshot;
    }

    public static VideoSnapshot from(DailyStatistic dailyStatistic) {
        VideoSnapshot videoSnapshot = new VideoSnapshot();
        videoSnapshot.videoViewCount = dailyStatistic.getVideoViewCount();
        videoSnapshot.advertisementViewCount = dailyStatistic.getAdvertisementViewCount();
        videoSnapshot.snapshotDate = dailyStatistic.getStatisticDate();
        videoSnapshot.videoId = dailyStatistic.getVideoId();
        return videoSnapshot;
    }

    public void updateSnapshot(DailyStatistic dailyStatistic) {
        this.videoViewCount += dailyStatistic.getVideoViewCount();
        this.advertisementViewCount += dailyStatistic.getAdvertisementViewCount();
        this.snapshotDate = dailyStatistic.getStatisticDate();
    }
}

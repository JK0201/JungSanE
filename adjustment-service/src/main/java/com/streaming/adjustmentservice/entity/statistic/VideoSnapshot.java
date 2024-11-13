package com.streaming.adjustmentservice.entity.statistic;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "videoSnapshot")
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
    private LocalDateTime snapshotDate;

    public static VideoSnapshot fromStatistic(DailyStatistic dailyStatistic) {
        VideoSnapshot videoSnapshot = new VideoSnapshot();
        videoSnapshot.videoViewCount = dailyStatistic.getVideoViewCount();
        videoSnapshot.advertisementViewCount = dailyStatistic.getAdvertisementViewCount();
        videoSnapshot.snapshotDate = dailyStatistic.getStatisticDate();
        videoSnapshot.videoId = dailyStatistic.getVideoId();
        return videoSnapshot;
    }

//    public void updateViewCount(DailyStatistic dailyStatistic) {
//        this.videoViewCount += dailyStatistic.getVideoViewCount();
//        this.advertisementViewCount += dailyStatistic.getAdvertisementViewCount();
//    }
//
//    public void updateDate(LocalDate targetDate) {
//        this.snapshotDate = targetDate;
//    }

}

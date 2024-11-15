package com.streaming.adjustmentservice.entity.settlement;

import com.streaming.adjustmentservice.entity.statistic.DailyStatistic;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
    private LocalDate snapshotDate;

    public static VideoSnapshot fromStatistic(DailyStatistic dailyStatistic) {
        VideoSnapshot videoSnapshot = new VideoSnapshot();
        videoSnapshot.videoViewCount = dailyStatistic.getVideoViewCount();
        videoSnapshot.advertisementViewCount = dailyStatistic.getAdvertisementViewCount();
        videoSnapshot.snapshotDate = dailyStatistic.getStatisticDate();
        videoSnapshot.videoId = dailyStatistic.getVideoId();
        return videoSnapshot;
    }

//    public static VideoSnapshot test(long videoId, long videoViewCount, long advertisementViewCount, LocalDate yesterday) {
//        VideoSnapshot videoSnapshot = new VideoSnapshot();
//        videoSnapshot.videoId = videoId;
//        videoSnapshot.videoViewCount = videoViewCount;
//        videoSnapshot.advertisementViewCount = advertisementViewCount;
//        videoSnapshot.snapshotDate = yesterday;
//        return videoSnapshot;
//    }
    
    public void updateSnapshot(DailyStatistic dailyStatistic) {
        this.videoViewCount += dailyStatistic.getVideoViewCount();
        this.advertisementViewCount += dailyStatistic.getAdvertisementViewCount();
        this.snapshotDate = dailyStatistic.getStatisticDate();
    }
}

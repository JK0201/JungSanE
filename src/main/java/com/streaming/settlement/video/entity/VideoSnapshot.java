package com.streaming.settlement.video.entity;

import com.streaming.settlement.adjustment.entity.DailyStatistic;
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
    private Long videoViewCount;

    @Column(nullable = false)
    private Long advertisementViewCount;

    @Column(nullable = false)
    private LocalDate snapshotDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static VideoSnapshot fromVideo(Video video) {
        VideoSnapshot videoSnapshot = new VideoSnapshot();
        videoSnapshot.videoViewCount = 0L;
        videoSnapshot.advertisementViewCount = 0L;
        videoSnapshot.snapshotDate = LocalDate.now();
        videoSnapshot.video = video;
        return videoSnapshot;
    }
    
    public void updateViewCount(DailyStatistic dailyStatistic) {
        this.videoViewCount += dailyStatistic.getVideoViewCount();
        this.advertisementViewCount += dailyStatistic.getAdvertisementViewCount();
    }

    public void updateDate(LocalDate targetDate) {
        this.snapshotDate = targetDate;
    }

}

package com.streaming.videoservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "videoAdvertisements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoAdvertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_advertisment_id")
    private Long id;

    @Column(nullable = false)
    private Long playbackTime;

    @Column(nullable = false)
    private Long accumulatedViewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static VideoAdvertisement fromVideo(Video video, Long playbackTime) {
        VideoAdvertisement videoAdvertisement = new VideoAdvertisement();
        videoAdvertisement.playbackTime = playbackTime;
        videoAdvertisement.accumulatedViewCount = 0L;
        videoAdvertisement.video = video;
        return videoAdvertisement;
    }

    /**
     * 영상 누적 조회수 +1 (업데이트)
     */
    public void addAccumulatedViewCount() {
        this.accumulatedViewCount++;
    }
}

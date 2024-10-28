package com.streaming.settlement.advertisement.entity;

import com.streaming.settlement.video.entity.Video;
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
    private Long advertisementTime;

    @Column(nullable = false)
    private Long accumulatedViewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static VideoAdvertisement fromVideo(Video video, Long advertisementTime) {
        VideoAdvertisement videoAdvertisement = new VideoAdvertisement();
        videoAdvertisement.advertisementTime = advertisementTime;
        videoAdvertisement.accumulatedViewCount = 0L;
        videoAdvertisement.video = video;
        return videoAdvertisement;
    }
}

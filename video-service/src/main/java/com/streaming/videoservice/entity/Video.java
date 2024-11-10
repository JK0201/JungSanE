package com.streaming.videoservice.entity;

import com.streaming.common.entity.Timestamped;
import com.streaming.videoservice.dto.request.VideoPublish;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "videos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Video extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_id")
    private Long id;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long playbackTime;

    @Column(nullable = false)
    private Long accumulatedViewCount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus status;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoAdvertisement> videoAdvertisementList = new ArrayList<>();

    public static Video fromPublish(VideoPublish videoPublish, Long userId) {
        Video video = new Video();
        video.title = videoPublish.getTitle();
        video.description = videoPublish.getDescription();
        video.playbackTime = videoPublish.getPlaybackTime();
        video.accumulatedViewCount = 0L;
        video.status = VideoStatus.ACTIVE;
        video.uploaderId = userId;
        return video;
    }

    /**
     * 영상 등록시 @PrePersist를 사용해 5분 단위로 계산 후, 광고 생성
     */
    @PrePersist
    protected void createAdvertisement() {
        generateAdvertisement();
    }

    public void incrementAccumulatedViewCount() {
        this.accumulatedViewCount++;
    }

    /**
     * 5분 단위로 광고 등록 (연관관계 편의 메서드 + Cascade 전파)
     */
    private void generateAdvertisement() {
        int advertisementCount = (int) (playbackTime / 300);
        for (int i = 1; i <= advertisementCount; i++) {
            videoAdvertisementList.add(VideoAdvertisement
                    .fromVideo(this, i * 300L));
        }
    }
}

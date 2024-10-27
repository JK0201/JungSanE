package com.streaming.settlement.video.entity;

import com.streaming.settlement.user.entity.User;
import com.streaming.settlement.video.dto.VideoPublish;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Long playbackTime;

    @Column(nullable = false)
    private Long accumulatedViewCount;

    @Column(nullable = false)
    private Long accumulatedPlaybackTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VideoStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Video fromPublish(VideoPublish videoPublish, User user) {
        Video video = new Video();
        video.title = videoPublish.getTitle();
        video.description = videoPublish.getDescription();
        video.playbackTime = videoPublish.getPlaybackTime();
        video.accumulatedViewCount = 0L;
        video.accumulatedPlaybackTime = 0L;
        video.status = VideoStatus.ACTIVE;
        video.user = user;
        return video;
    }

    public void addAccumulatedViewCount() {
        this.accumulatedViewCount++;
    }

    public void addAccumulatedPlaybackTime(Long playedTime) {
        this.accumulatedPlaybackTime += playedTime;
    }
}

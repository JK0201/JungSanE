package com.streaming.settlement.video.entity;

import com.streaming.settlement.user.entity.UserEntity;
import com.streaming.settlement.video.dto.Video;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "videos")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoEntity {

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
    private UserEntity userEntity;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public static VideoEntity from(Video video) {
        VideoEntity videoEntity = new VideoEntity();
        videoEntity.id = video.getId();
        videoEntity.title = video.getTitle();
        videoEntity.description = video.getDescription();
        videoEntity.playbackTime = video.getPlaybackTime();
        videoEntity.accumulatedViewCount = video.getAccumulatedViewCount();
        videoEntity.accumulatedPlaybackTime = video.getAccumulatedPlaybackTime();
        videoEntity.status = video.getStatus();
        videoEntity.userEntity = UserEntity.from(video.getUser());
        videoEntity.createdAt = video.getCreatedAt();
        videoEntity.modifiedAt = video.getModifiedAt();

        return videoEntity;
    }

    public Video toModel() {
        return Video.builder()
                .id(id)
                .title(title)
                .description(description)
                .playbackTime(playbackTime)
                .accumulatedViewCount(accumulatedViewCount)
                .accumulatedPlaybackTime(accumulatedPlaybackTime)
                .status(status)
                .user(userEntity.toModel())
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();
    }
}

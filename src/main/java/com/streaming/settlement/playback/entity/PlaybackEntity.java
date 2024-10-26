package com.streaming.settlement.playback.entity;

import com.streaming.settlement.playback.dto.Playback;
import com.streaming.settlement.user.entity.UserEntity;
import com.streaming.settlement.video.entity.VideoEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "playbacks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaybackEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playback_id")
    private Long id;

    @Column(nullable = false)
    private Long lastPlayTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private VideoEntity videoEntity;

    @Column(updatable = false)
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

    public static PlaybackEntity from(Playback playback) {
        PlaybackEntity playbackEntity = new PlaybackEntity();
        playbackEntity.id = playback.getId();
        playbackEntity.lastPlayTime = playback.getLastPlayTime();
        playbackEntity.userEntity = UserEntity.from(playback.getUser());
        playbackEntity.videoEntity = VideoEntity.fromPlayback(playback.getVideo());
        playbackEntity.createdAt = playback.getCreatedAt();
        playbackEntity.modifiedAt = playback.getModifiedAt();

        return playbackEntity;
    }

    public Playback toModel() {
        return Playback.builder()
                .id(id)
                .lastPlayTime(lastPlayTime)
                .createdAt(createdAt)
                .modifiedAt(modifiedAt)
                .build();
    }
}

package com.streaming.adjustmentservice.entity.statistic;

import com.streaming.common.dto.event.PlaybackEvent;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "playback_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlaybackLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playback_id")
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private Long videoPlayedTime;

    @Column(nullable = false)
    private Long advertisementViewCount;

    @Column(nullable = false)
    private Boolean isNewView;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static PlaybackLog from(PlaybackEvent playbackEvent) {
        PlaybackLog playbackLog = new PlaybackLog();
        playbackLog.videoId = playbackEvent.getVideoId();
        playbackLog.uploaderId = playbackEvent.getUploaderId();
        playbackLog.videoPlayedTime = playbackEvent.getVideoPlayedTime();
        playbackLog.advertisementViewCount = playbackEvent.getAdvertisementViewCount();
        playbackLog.isNewView = playbackEvent.getIsNewView();
        playbackLog.createdAt = playbackEvent.getCreatedAt();
        return playbackLog;
    }
}

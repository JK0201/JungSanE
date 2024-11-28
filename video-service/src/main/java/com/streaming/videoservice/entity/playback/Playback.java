package com.streaming.videoservice.entity.playback;

import com.streaming.common.entity.Timestamped;
import com.streaming.videoservice.entity.video.Video;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "playbacks")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playback extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "playback_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long lastPlayPosition;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaybackStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static Playback of(Long userId, Video video) {
        Playback playback = new Playback();
        playback.userId = userId;
        playback.lastPlayPosition = 0L;
        playback.status = PlaybackStatus.PROGRESS;
        playback.video = video;
        return playback;
    }

    /**
     * 유저 마지막 재생 시간 업데이트
     *
     * @param position (Long)
     */
    public void updateLastPosition(Long position) {
        this.lastPlayPosition = position;
    }

    /**
     * 유저 영상 재생 상태 업데이트
     *
     * @param status (PlaybackStatus)
     */
    public void updateStatus(PlaybackStatus status) {
        this.status = status;
    }
}

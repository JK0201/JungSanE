package com.streaming.settlement.playback.entity;

import com.streaming.settlement.user.entity.User;
import com.streaming.settlement.video.entity.Timestamped;
import com.streaming.settlement.video.entity.Video;
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
    private Long videoPlayedTime;

    @Column(nullable = false)
    private Long lastPlayPosition;

    @Column(nullable = false)
    private Long advertisementViewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static Playback createUserPlayback(Long lastPlayPosition, User user, Video video) {
        Playback playback = new Playback();
        playback.videoPlayedTime = 0L;
        playback.lastPlayPosition = lastPlayPosition;
        playback.advertisementViewCount = 0L;
        playback.user = user;
        playback.video = video;
        return playback;
    }

    public void updateVideoPlayedTime(Long videoPlayedTime) {
        this.videoPlayedTime = videoPlayedTime;
    }

    public void incrementAdvertisementViewCount() {
        this.advertisementViewCount++;
    }
}

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
    private Long lastPlayTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static Playback createUserPlayback(User user, Video video, long lastPlayTime) {
        Playback playback = new Playback();
        playback.user = user;
        playback.video = video;
        playback.lastPlayTime = lastPlayTime;
        playback.video.increaseAccumulatedViewCount();
        return playback;
    }

    public void updateUserPlayTime() {
        if (this.lastPlayTime >= video.getPlaybackTime()) {
            this.lastPlayTime = 0L;
        }
    }
}

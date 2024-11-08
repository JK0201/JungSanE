//package com.streaming.adjustmentservice.entity;
//
//import com.streaming.common.entity.Timestamped;
//import com.streaming.userservice.entity.User;
//import com.streaming.videoservice.entity.Video;
//import jakarta.persistence.*;
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Getter
//@Table(name = "playbacks")
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class PlaybackLog extends Timestamped {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "playback_id")
//    private Long id;
//
//    @Column(nullable = false)
//    private Long videoPlayedTime;
//
//    @Column(nullable = false)
//    private Long lastPlayPosition;
//
//    @Column(nullable = false)
//    private Long advertisementViewCount;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "video_id")
//    private Video video;
//
//    public static PlaybackLog createUserPlayback(Long lastPlayPosition, User user, Video video) {
//        PlaybackLog playback = new PlaybackLog();
//        playback.videoPlayedTime = 0L;
//        playback.lastPlayPosition = lastPlayPosition;
//        playback.advertisementViewCount = 0L;
//        playback.user = user;
//        playback.video = video;
//        return playback;
//    }
//
//    public void updateVideoPlayedTime(Long videoPlayedTime) {
//        this.videoPlayedTime = videoPlayedTime;
//    }
//
//    public void incrementAdvertisementViewCount() {
//        this.advertisementViewCount++;
//    }
//}

package com.streaming.settlement.video.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private Long playbackTime;

    @Column(nullable = false)
    private Long accumulatedViewCount;

    @Column(nullable = false)
    private Long accumulatedPlaybackTime;

    
}

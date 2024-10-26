package com.streaming.settlement.advertisement.entity;

import com.streaming.settlement.video.entity.VideoEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "videoAdvertisements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoAdvertisementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_advertisment_id")
    private Long id;

    @Column(nullable = false)
    private Long advertisementTime;

    @Column(nullable = false)
    private Long accumulatedViewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private VideoEntity videoEntity;
}

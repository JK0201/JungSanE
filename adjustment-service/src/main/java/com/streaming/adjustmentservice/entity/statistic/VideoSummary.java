package com.streaming.adjustmentservice.entity.statistic;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(
        name = "video_summary",
        indexes = {
                @Index(
                        name = "idx_period_view",
                        columnList = "period_type, video_view_count DESC"
                ),
                @Index(
                        name = "idx_period_playtime",
                        columnList = "period_type, video_played_time DESC"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_video_id_period",
                        columnNames = {"video_id", "period_type"}
                )
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VideoSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_summary_id")
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Long uploaderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PeriodType periodType;

    @Column(nullable = false)
    private Long videoViewCount;

    @Column(nullable = false)
    private Long videoPlayedTime;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

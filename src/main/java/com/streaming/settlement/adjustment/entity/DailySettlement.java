package com.streaming.settlement.adjustment.entity;

import com.streaming.settlement.video.entity.Video;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "dailySettlements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_settlement_id")
    private Long id;

    @Column(nullable = false)
    private BigDecimal videoRevenue;

    @Column(nullable = false)
    private BigDecimal advertisementRevenue;

    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private LocalDate settlementDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id")
    private Video video;

    public static DailySettlement from(BigDecimal videoRevenue, BigDecimal advertisementRevenue, Video video) {
        DailySettlement dailySettlement = new DailySettlement();
        dailySettlement.videoRevenue = videoRevenue;
        dailySettlement.advertisementRevenue = advertisementRevenue;
        dailySettlement.totalRevenue = videoRevenue.add(advertisementRevenue);
        dailySettlement.settlementDate = LocalDate.now().minusDays(1);
        dailySettlement.video = video;
        return dailySettlement;
    }
}

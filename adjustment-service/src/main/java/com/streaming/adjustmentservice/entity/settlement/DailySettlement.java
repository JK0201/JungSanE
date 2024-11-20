package com.streaming.adjustmentservice.entity.settlement;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Table(name = "dailySettlement")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailySettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_settlement_id")
    private Long id;

    @Column(nullable = false)
    private Long videoId;

    @Column(nullable = false)
    private Long uploaderId;

    @Column(nullable = false)
    private BigDecimal videoRevenue;

    @Column(nullable = false)
    private BigDecimal advertisementRevenue;

    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private LocalDate settlementDate;

    public static DailySettlement fromRevenue(BigDecimal videoRevenue, BigDecimal advertisementRevenue, LocalDate settlementDate, Long videoId, Long uploaderId) {
        DailySettlement dailySettlement = new DailySettlement();
        dailySettlement.videoRevenue = videoRevenue;
        dailySettlement.advertisementRevenue = advertisementRevenue;
        dailySettlement.totalRevenue = videoRevenue.add(advertisementRevenue);
        dailySettlement.settlementDate = settlementDate;
        dailySettlement.videoId = videoId;
        dailySettlement.uploaderId = uploaderId;
        return dailySettlement;
    }
}

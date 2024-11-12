package com.streaming.adjustmentservice.entity.settlement;

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
    private Long videoId;

    @Column(nullable = false)
    private BigDecimal videoRevenue;

    @Column(nullable = false)
    private BigDecimal advertisementRevenue;

    @Column(nullable = false)
    private BigDecimal totalRevenue;

    @Column(nullable = false)
    private LocalDate settlementDate;

    public static DailySettlement from(BigDecimal videoRevenue, BigDecimal advertisementRevenue, Long videoId) {
        DailySettlement dailySettlement = new DailySettlement();
        dailySettlement.videoRevenue = videoRevenue;
        dailySettlement.advertisementRevenue = advertisementRevenue;
        dailySettlement.totalRevenue = videoRevenue.add(advertisementRevenue);
        dailySettlement.settlementDate = LocalDate.now().minusDays(1);
        dailySettlement.videoId = videoId;
        return dailySettlement;
    }
}

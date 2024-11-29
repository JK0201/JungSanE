package com.streaming.adjustmentservice.service;

import com.streaming.adjustmentservice.controller.port.AdjustmentQueryService;
import com.streaming.adjustmentservice.dto.request.DateRange;
import com.streaming.adjustmentservice.dto.response.SettlementResponse;
import com.streaming.adjustmentservice.dto.response.StatisticResponse;
import com.streaming.adjustmentservice.dto.response.TopVideosResponse;
import com.streaming.adjustmentservice.entity.settlement.DailySettlement;
import com.streaming.adjustmentservice.entity.statistic.PeriodType;
import com.streaming.adjustmentservice.service.port.DailySettlementRepository;
import com.streaming.adjustmentservice.service.port.VideoSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdjustmentQueryServiceImpl implements AdjustmentQueryService {

    private final VideoSummaryRepository videoSummaryRepository;
    private final DailySettlementRepository dailySettlementRepository;

    @Override
//    @Cacheable(value = "top5Videos", key = "'period:' + #period + ':type:' + #type")
    public StatisticResponse getTop5Videos(String period, String type) {
        DateRange dateRange = calculateDateRange(period);

        log.info("==============================");
        log.info("Period: {}", dateRange.getPeriodType());
        log.info("START_DATE: {} - END_DATE: {}", dateRange.getStartDate(), dateRange.getEndDate());
        log.info("==============================");

        List<TopVideosResponse> top5VideoList = switch (type) {
            case "view" -> videoSummaryRepository.findTopViewedVideos(
                    dateRange.getStartDate(),
                    dateRange.getEndDate(),
                    dateRange.getPeriodType()
            );
            case "playtime" -> videoSummaryRepository.findTopPlayedVideos(
                    dateRange.getStartDate(),
                    dateRange.getEndDate(),
                    dateRange.getPeriodType()
            );
            default -> throw new IllegalArgumentException("잘못된 통계 타입 입니다.");
        };

        return StatisticResponse.of(
                type,
                dateRange,
                top5VideoList
        );
    }

    @Override
    public SettlementResponse getSettlements(Long uploaderId, String period) {
        DateRange dateRange = calculateDateRange(period);

        log.info("==============================");
        log.info("Period: {}", dateRange.getPeriodType());
        log.info("START_DATE: {} - END_DATE: {}", dateRange.getStartDate(), dateRange.getEndDate());
        log.info("==============================");

        List<DailySettlement> settlementList = dailySettlementRepository.findVideoSettlements(
                uploaderId,
                dateRange.getStartDate(),
                dateRange.getEndDate()
        );

        return SettlementResponse.of(
                dateRange,
                uploaderId,
                settlementList
        );
    }

    private DateRange calculateDateRange(String period) {
        LocalDate now = LocalDate.now();

        return switch (period) {
            case "daily" -> DateRange.of(now, now, PeriodType.DAILY);

            case "weekly" -> DateRange.of(
                    now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
                    now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)),
                    PeriodType.WEEKLY
            );
            case "monthly" -> DateRange.of(
                    now.with(TemporalAdjusters.firstDayOfMonth()),
                    now.with(TemporalAdjusters.lastDayOfMonth()),
                    PeriodType.MONTHLY
            );
            default -> throw new IllegalArgumentException("잘못된 조회 기간 입니다.");
        };
    }
}

package com.streaming.adjustmentservice.service;

import com.streaming.adjustmentservice.controller.port.AdjustmentQueryService;
import com.streaming.adjustmentservice.dto.request.DateRange;
import com.streaming.adjustmentservice.dto.response.SettlementResponse;
import com.streaming.adjustmentservice.dto.response.StatisticResponse;
import com.streaming.adjustmentservice.dto.response.Top5VideosWrapper;
import com.streaming.adjustmentservice.service.port.DailySettlementRepository;
import com.streaming.adjustmentservice.service.port.DailyStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdjustmentQueryServiceImpl implements AdjustmentQueryService {

    private final DailyStatisticRepository dailyStatisticRepository;
    private final DailySettlementRepository dailySettlementRepository;

    @Override
    public StatisticResponse getTop5Videos(String period, String type) {
        DateRange dateRange = calculateDateRange(period);
        List<Top5VideosWrapper> top5VideoList;

        if (period.equals("daily")) {
            top5VideoList = type.equals("view")
                    ? dailyStatisticRepository.findDailyTop5VideosByViewCount(dateRange.getStartDate())
                    : dailyStatisticRepository.findDailyTop5VideosByPlayedTime(dateRange.getStartDate());
        } else {
            top5VideoList = type.equals("view")
                    ? dailyStatisticRepository.findRangeTop5VideosByViewCount(dateRange.getStartDate(), dateRange.getEndDate())
                    : dailyStatisticRepository.findRangeTop5VideosByPlayedTime(dateRange.getStartDate(), dateRange.getEndDate());
        }

        return StatisticResponse.of(
                period,
                type,
                dateRange,
                top5VideoList
        );
    }

    @Override
    public SettlementResponse getSettlements(Long userId, String period) {
        DateRange dateRange = calculateDateRange(period);

//        List<DailySettlement> settlementList = dailySettlementRepository.find

//        return SettlementResponse.of(
//                uploaderSettlement.getPeriod(),
//                dateRange,
//                userId,
//                settlementList
//        );
        return null;
    }

    private DateRange calculateDateRange(String period) {
        LocalDate now = LocalDate.now();

        return switch (period) {
            case "daily" -> DateRange.of(now, now);

            case "weekly" -> DateRange.of(
//                    now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)),
//                    now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                    now.minusDays(6), now
            );
            case "monthly" -> DateRange.of(
//                    now.with(TemporalAdjusters.firstDayOfMonth()),
//                    now.with(TemporalAdjusters.lastDayOfMonth())
                    now.minusDays(29), now
            );
            default -> throw new IllegalArgumentException("잘못된 조회 기간 입니다.");
        };
    }
}

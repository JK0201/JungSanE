package com.streaming.adjustmentservice.service;

import com.streaming.adjustmentservice.controller.port.StatisticQueryService;
import com.streaming.adjustmentservice.dto.StatisticResponse;
import com.streaming.adjustmentservice.service.port.DailyStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticQueryServiceImpl implements StatisticQueryService {

    private final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    private final DailyStatisticRepository dailyStatisticRepository;

    @Override
    public List<StatisticResponse> dailyTopViewed() {
//        List<StatisticResponse> dailyTop5 = dailyStatisticRepository
//                .findDailyTop5ByVideoViewCount(YESTERDAY);
        return null;
    }
}

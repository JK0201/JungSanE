package com.streaming.adjustmentservice.service;

import com.streaming.adjustmentservice.dto.StatisticResponse;
import com.streaming.adjustmentservice.repository.DailyStatisticRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticQueryService {

    private final LocalDate YESTERDAY = LocalDate.now().minusDays(1);

    private final DailyStatisticRepository dailyStatisticRepository;

    public List<StatisticResponse> dailyTopViewed() {
//        List<StatisticResponse> dailyTop5 = dailyStatisticRepository
//                .findDailyTop5ByVideoViewCount(YESTERDAY);
        return null;
    }
}

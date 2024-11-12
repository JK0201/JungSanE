package com.streaming.adjustmentservice.controller.port;

import com.streaming.adjustmentservice.dto.StatisticResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface StatisticQueryService {

    List<StatisticResponse> dailyTopViewed();
}

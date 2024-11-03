package com.streaming.adjustmentservice.controller;

import com.streaming.adjustmentservice.dto.StatisticResponse;
import com.streaming.adjustmentservice.service.StatisticQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistic")
public class StatisticController {

    private final StatisticQueryService statisticQueryService;

    @GetMapping("/daily/views")
    public ResponseEntity<List<StatisticResponse>> dailyTopViewed() {
        System.out.println("request");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(statisticQueryService.dailyTopViewed());
    }
}

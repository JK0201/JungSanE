package com.streaming.adjustmentservice.controller;

import com.streaming.adjustmentservice.controller.port.AdjustmentQueryService;
import com.streaming.adjustmentservice.dto.response.SettlementResponse;
import com.streaming.adjustmentservice.dto.response.StatisticResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AdjustmentController {

    private final AdjustmentQueryService adjustmentQueryService;

    @GetMapping("/statistic")
    public ResponseEntity<StatisticResponse> getTop5Videos(
            @RequestParam String period,
            @RequestParam String type
    ) {
        StatisticResponse StatisticResponse = adjustmentQueryService.getTop5Videos(period, type);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StatisticResponse);
    }

    // FIXME 토큰 헤더로 파싱해서 유저 가저오는걸로 변경
    @GetMapping("/settlement")
    public ResponseEntity<SettlementResponse> getSettlements(
            HttpServletRequest request,
            @RequestParam String period
    ) {
        Long userId = 1L;
        SettlementResponse settlementResponse = adjustmentQueryService.getSettlements(userId, period);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(settlementResponse);
    }
}

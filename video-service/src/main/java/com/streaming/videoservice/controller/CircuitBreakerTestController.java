package com.streaming.videoservice.controller;

import com.streaming.videoservice.config.open_feign.CircuitBreakerTestServiceClient;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CircuitBreakerTestController {

    @Qualifier("circuitBreakerTestServiceFallback")
    private final CircuitBreakerTestServiceClient circuitBreakerTestServiceClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @GetMapping("/test/case1")
    public ResponseEntity<String> testCase1() {
        return circuitBreakerTestServiceClient.callCase1();
    }

    @GetMapping("/test/case2")
    public ResponseEntity<String> testCase2() {
        return circuitBreakerTestServiceClient.callCase2();
    }

    @GetMapping("/test/case3")
    public ResponseEntity<String> testCase3() {
        return circuitBreakerTestServiceClient.callCase3();
    }

    /**
     * 1초마다 Adjustment Service에 있는
     * ErrorfulController에 요청
     */
//    @Scheduled(cron = "* * * * * *")
//    public void testEndpoints() {
//        // 단일 케이스
//        testCase("2");
//
//        // N개 케이스
//        Stream.of("1", "2", "3")
//                .parallel()
//                .forEach(this::testCase);
//    }
    private void testCase(String caseNumber) {
        String circuitName = String.format("CircuitBreakerTestServiceClientcallCase%s", caseNumber);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitName);

        logCircuitState(caseNumber, circuitBreaker.getState());

        try {
            ResponseEntity<String> response = switch (caseNumber) {
                case "1" -> circuitBreakerTestServiceClient.callCase1();
                case "2" -> circuitBreakerTestServiceClient.callCase2();
                default -> circuitBreakerTestServiceClient.callCase3();
            };

            log.info("[Case{}] Response: {}", caseNumber, response.getBody());
        } catch (Exception ex) {
            log.error("[Case{}] Waiting for recovery...", caseNumber);
        }
    }

    private void logCircuitState(String caseNumber, CircuitBreaker.State state) {
        String logMessage = String.format("[Case%s] Circuit Breaker State: %s", caseNumber, state);

        switch (state) {
            case OPEN -> log.error(logMessage);
            case HALF_OPEN -> log.warn(logMessage);
            default -> log.info(logMessage);
        }
    }
}

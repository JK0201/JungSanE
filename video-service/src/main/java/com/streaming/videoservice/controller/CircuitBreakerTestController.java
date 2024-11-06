package com.streaming.videoservice.controller;

import com.streaming.videoservice.client.CircuitBreakerTestServiceClient;
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

//    /**
//     * 1초마다 Adjustment Service에 있는
//     * ErrorfulController에 요청
//     */
//    @Scheduled(cron = "* * * * * *")
//    public void testEndpoints() {
//        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("circuitBreakerTest");
//
//        switch (circuitBreaker.getState()) {
//            case CircuitBreaker.State.OPEN:
//                log.error("=== Circuit Breaker State: {} ===", circuitBreaker.getState());
//                break;
//
//            case CircuitBreaker.State.HALF_OPEN:
//                log.warn("=== Circuit Breaker State: {} ===", circuitBreaker.getState());
//                break;
//
//            default:
//                log.info("=== Circuit Breaker State: {} ===", circuitBreaker.getState());
//        }
//
//        // case1
//        try {
//            ResponseEntity<String> response1 = circuitBreakerTestServiceClient.callCase1();
//            log.info("Case1 Response: {}", response1.getBody());
//        } catch (Exception ex) {
//            log.error("Case1 Error: {}", ex.getMessage());
//        }
//
//        // case2
//        try {
//            ResponseEntity<String> response2 = circuitBreakerTestServiceClient.callCase1();
//            log.info("Case2 Response: {}", response2.getBody());
//        } catch (Exception ex) {
//            log.error("Case2 Error: {}", ex.getMessage());
//        }
//
//        // case3
//        try {
//            ResponseEntity<String> response3 = circuitBreakerTestServiceClient.callCase1();
//            log.info("Case3 Response: {}", response3.getBody());
//        } catch (Exception ex) {
//            log.error("Case3 Error: {}", ex.getMessage());
//        }
//    }
}

package com.streaming.videoservice.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CircuitBreakerTestServiceFallback implements CircuitBreakerTestServiceClient {

    @Override
    public ResponseEntity<String> callCase1() {
        log.warn("[Circuit Breaker] Fallback: case1 called");
        return ResponseEntity.ok("[Circuit Breaker] Fallback Response for Case1");
    }

    @Override
    public ResponseEntity<String> callCase2() {
        log.warn("[Circuit Breaker] Fallback: case2 called");
        return ResponseEntity.ok("[Circuit Breaker] Fallback Response for Case2");
    }

    @Override
    public ResponseEntity<String> callCase3() {
        log.warn("[Circuit Breaker] Fallback: case3 called");
        return ResponseEntity.ok("[Circuit Breaker] Fallback Response for Case3");
    }
}

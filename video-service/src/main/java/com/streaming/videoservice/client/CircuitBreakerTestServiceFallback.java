package com.streaming.videoservice.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CircuitBreakerTestServiceFallback implements CircuitBreakerTestServiceClient {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public ResponseEntity<String> callCase1() {
        return handleFallback("1");
    }

    @Override
    public ResponseEntity<String> callCase2() {
        return handleFallback("2");
    }

    @Override
    public ResponseEntity<String> callCase3() {
        return handleFallback("3");
    }

    private ResponseEntity<String> handleFallback(String caseNumber) {
        String circuitName = String.format("CircuitBreakerTestServiceClientcallCase%s", caseNumber);
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitName);

        log.warn("[Case{} Fallback triggered] Metrics: {}",
                caseNumber,
                String.format("FailureRate: %.2f%%",
                        circuitBreaker.getMetrics().getFailureRate()));

        return ResponseEntity.ok("Fallback Response");
    }
}

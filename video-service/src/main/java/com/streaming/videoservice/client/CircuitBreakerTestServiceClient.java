package com.streaming.videoservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "adjustment-service", fallback = CircuitBreakerTestServiceFallback.class)
public interface CircuitBreakerTestServiceClient {

    @GetMapping("/errorful/case1")
    @CircuitBreaker(name = "circuitBreakerTest")
    @Retry(name = "circuitBreakerTest")
    ResponseEntity<String> callCase1();

    @GetMapping("/errorful/case2")
    @CircuitBreaker(name = "circuitBreakerTest")
    @Retry(name = "circuitBreakerTest")
    ResponseEntity<String> callCase2();

    @GetMapping("/errorful/case3")
    @CircuitBreaker(name = "circuitBreakerTest")
    @Retry(name = "circuitBreakerTest")
    ResponseEntity<String> callCase3();
}

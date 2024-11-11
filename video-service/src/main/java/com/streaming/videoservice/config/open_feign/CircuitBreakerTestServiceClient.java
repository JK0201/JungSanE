package com.streaming.videoservice.config.open_feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "adjustment-service",
        fallback = CircuitBreakerTestServiceFallback.class)
public interface CircuitBreakerTestServiceClient {

    /**
     * Feign Client를 사용하여 adjustment-service에 요청
     * ErrorfulController case1 요청
     *
     * @return ResponseEntity
     */
    @GetMapping("/errorful/case1")
    @CircuitBreaker(name = "CircuitBreakerTestServiceClientcallCase1")
    @Retry(name = "CircuitBreakerTestServiceClientcallCase1")
    ResponseEntity<String> callCase1();

    /**
     * Feign Client를 사용하여 adjustment-service에 요청
     * ErrorfulController case2 요청
     *
     * @return ResponseEntity
     */
    @GetMapping("/errorful/case2")
    @CircuitBreaker(name = "CircuitBreakerTestServiceClientcallCase2")
    @Retry(name = "CircuitBreakerTestServiceClientcallCase2")
    ResponseEntity<String> callCase2();

    /**
     * Feign Client를 사용하여 adjustment-service에 요청
     * ErrorfulController case3 요청
     *
     * @return ResponseEntity
     */
    @GetMapping("/errorful/case3")
    @CircuitBreaker(name = "CircuitBreakerTestServiceClientcallCase3")
    @Retry(name = "CircuitBreakerTestServiceClientcallCase3")
    ResponseEntity<String> callCase3();
}

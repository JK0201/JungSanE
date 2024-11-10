package com.streaming.adjustmentservice.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.Random;

@Slf4j
@RestController
public class ErrorfulController {

    // 5% 확률 500에러 테스트
    @GetMapping("/errorful/case1")
    public ResponseEntity<String> case1() {
        log.info("[Case1] request from video-server");

        // Simulate 5% chance of 500 error
        if (new Random().nextInt(100) < 5) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Normal response");
    }

    // 매분 첫 10초 딜레이 테스트
    @GetMapping("/errorful/case2")
    public ResponseEntity<String> case2() {
        log.info("[Case2] request from video-server");

        // Simulate blocking requests every first 10 seconds
        LocalTime currentTime = LocalTime.now();
        int currentSecond = currentTime.getSecond();

        if (currentSecond < 10) {
            // Simulate a delay (block) for 10 seconds
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Service Unavailable");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Normal response");
    }

    // 매분 첫 10초 500에러 테스트
    @GetMapping("/errorful/case3")
    public ResponseEntity<String> case3() {
        log.info("[Case3] request from video-server");

        // Simulate 500 error every first 10 seconds
        LocalTime currentTime = LocalTime.now();
        int currentSecond = currentTime.getSecond();

        if (currentSecond < 10) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error");
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Normal response");
    }
}

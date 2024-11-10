package com.streaming.videoservice.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public Boolean checkFirstAccess(String key, String value, long timeout, TimeUnit timeUnit) {
        log.info("Check for view count abuse : {}", key);
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }
}

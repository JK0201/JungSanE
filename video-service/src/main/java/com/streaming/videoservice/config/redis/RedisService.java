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

    /**
     * 동영상 조회수 어뷰징을 위해 Redis에 저장된 Key 검증
     *
     * @param key      "abuse:video:{videoId}:{userId}"     (String)
     * @param value    "1" (String)
     * @param timeout  30L (long)
     * @param timeUnit seconds (TimeUnit.SECONDS)
     * @return [ Key가 있을 경우 : 어뷰징 이므로 false 반환] / [ Key가 없을 경우 : 첫 시청이므로 true 반환 ]
     */
    public Boolean checkFirstAccess(String key, String value, long timeout, TimeUnit timeUnit) {
        log.info("Check for view count abuse : {}", key);
        return redisTemplate.opsForValue().setIfAbsent(key, value, timeout, timeUnit);
    }
}

package com.streaming.videoservice.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedissonLockFacade {

    private static final long WAIT_TIME = 2L;
    private static final long LEASE_TIME = 3L;
    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;

    public <T> T executeWithLock(String lockKey, Supplier<T> task) {
        RLock lock = redissonClient.getLock(lockKey);

        try {
            if (!lock.tryLock(WAIT_TIME, LEASE_TIME, TimeUnit.SECONDS)) {
                log.error("Failed to acquire lock for key: {} within {} seconds", lockKey, WAIT_TIME);
                throw new RuntimeException("Failed to acquire lock");
            }
            log.info("Lock acquired for key: {}", lockKey);
            return task.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            log.error("Interrupted while acquiring lock for key : {}", lockKey);
            throw new RuntimeException("Lock acquisition failed");
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("Lock released for key: {}", lockKey);
            }
        }
    }
}

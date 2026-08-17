package com.ticketpass.ticketservice.infrastructure.lock;

import com.ticketpass.ticketservice.domain.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService implements LockService {

    private final RedisLockRegistry redisLockRegistry;

    @Override
    public <T> T executeWithLock(String lockKey, long leaseTime, TimeUnit timeUnit, Supplier<T> task) {
        Lock lock = redisLockRegistry.obtain(lockKey);
        boolean acquired = false;
        try {
            log.info("Attempting to acquire lock for key: {}", lockKey);
            acquired = lock.tryLock(leaseTime, timeUnit);
            if (!acquired) {
                throw new RuntimeException("Could not acquire lock for key: " + lockKey);
            }
            log.info("Successfully acquired lock for key: {}", lockKey);
            return task.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while waiting for lock key: " + lockKey, e);
        } finally {
            if (acquired) {
                try {
                    lock.unlock();
                    log.info("Released lock for key: {}", lockKey);
                } catch (Exception e) {
                    log.error("Error while releasing lock for key: {}", lockKey, e);
                }
            }
        }
    }
}

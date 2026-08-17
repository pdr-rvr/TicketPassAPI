package com.ticketpass.ticketservice.domain.service;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public interface LockService {
    <T> T executeWithLock(String lockKey, long leaseTime, TimeUnit timeUnit, Supplier<T> task);
}

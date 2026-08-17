package com.ticketpass.ticketservice.infrastructure.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    private static final int MAX_REQUESTS_PER_MINUTE = 20;
    private static final String REDIS_PREFIX = "rate:limit:ip:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        } else {
            ipAddress = ipAddress.split(",")[0].trim();
        }
        String key = REDIS_PREFIX + ipAddress;

        try {
            Long count = redisTemplate.opsForValue().increment(key);
            if (count != null) {
                Long ttl = redisTemplate.getExpire(key);
                if (ttl != null && ttl == -1) {
                    redisTemplate.expire(key, Duration.ofMinutes(1));
                }
            }

            if (count != null && count > MAX_REQUESTS_PER_MINUTE) {
                log.warn("Rate limit exceeded for IP: {}. Requests: {}", ipAddress, count);
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Too many requests. Please try again in a minute.\"}");
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to check rate limit for IP: {}", ipAddress, e);
        }

        return true;
    }
}

package com.ticketpass.ticketservice.infrastructure.lock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.redis.util.RedisLockRegistry;

@Configuration
public class LockConfig {

    @Bean
    public RedisLockRegistry redisLockRegistry(RedisConnectionFactory redisConnectionFactory) {
        // Namespace "ticketpass-locks" with a default of 60 seconds lease time
        return new RedisLockRegistry(redisConnectionFactory, "ticketpass-locks", 60000);
    }
}

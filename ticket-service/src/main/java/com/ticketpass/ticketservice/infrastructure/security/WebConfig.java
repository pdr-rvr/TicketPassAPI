package com.ticketpass.ticketservice.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RateLimitingInterceptor rateLimitingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register rate limiter to protect reservation, auth and user endpoints
        registry.addInterceptor(rateLimitingInterceptor)
                .addPathPatterns("/reservations/**", "/auth/**", "/users/**");
    }
}

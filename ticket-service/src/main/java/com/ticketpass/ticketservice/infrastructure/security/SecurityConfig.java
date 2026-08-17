package com.ticketpass.ticketservice.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // Public authentication and sign up
                .requestMatchers("/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()

                // Public queries
                .requestMatchers(HttpMethod.GET, "/events/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/{id}").permitAll()

                // Monitoring & Docs
                .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()

                // Admin endpoints
                .requestMatchers(HttpMethod.POST, "/events").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/events/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/events/*/cancel").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")

                // Common user / client endpoints
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(com.ticketpass.ticketservice.domain.repository.UserRepository userRepository) {
        return username -> {
            com.ticketpass.ticketservice.domain.model.User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getEmail())
                    .password(user.getPassword() != null ? user.getPassword() : "")
                    .authorities(user.getRole())
                    .build();
        };
    }
}

package com.ticketpass.ticketservice.infrastructure.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.domain.repository.EventRepository;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.EventEntity;
import com.ticketpass.ticketservice.infrastructure.persistence.mapper.EventMapper;
import com.ticketpass.ticketservice.infrastructure.persistence.repository.JpaEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PostgresEventRepository implements EventRepository {

    private final JpaEventRepository jpaEventRepository;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String CACHE_KEY_PREFIX = "event:id:";
    private static final String CACHE_KEY_ALL = "events:all";
    private static final Duration CACHE_TTL = Duration.ofMinutes(5);

    @Override
    public Event save(Event event) {
        EventEntity entity = EventMapper.toEntity(event);
        EventEntity saved = jpaEventRepository.save(entity);
        Event domain = EventMapper.toDomain(saved);

        // Evict cache to maintain consistency, ensuring it runs post-commit if in active transaction
        if (org.springframework.transaction.support.TransactionSynchronizationManager.isActualTransactionActive()) {
            org.springframework.transaction.support.TransactionSynchronizationManager.registerSynchronization(
                new org.springframework.transaction.support.TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        evictCache(domain.getId());
                    }
                }
            );
        } else {
            evictCache(domain.getId());
        }

        return domain;
    }

    @Override
    public Optional<Event> findByIdForUpdate(Long id) {
        log.info("Pessimistic lock query for event ID: {}. Querying database directly", id);
        return jpaEventRepository.findByIdForUpdate(id)
                .map(EventMapper::toDomain);
    }

    @Override
    public Optional<Event> findById(Long id) {
        String key = CACHE_KEY_PREFIX + id;
        try {
            String cachedJson = redisTemplate.opsForValue().get(key);
            if (cachedJson != null) {
                log.info("Cache hit for event ID: {}", id);
                return Optional.of(objectMapper.readValue(cachedJson, Event.class));
            }
        } catch (Exception e) {
            log.error("Failed to query Redis cache for event ID: {}, querying DB directly", id, e);
        }

        log.info("Cache miss for event ID: {}. Querying database", id);
        Optional<EventEntity> entityOpt = jpaEventRepository.findById(id);
        if (entityOpt.isEmpty()) {
            return Optional.empty();
        }

        Event domain = EventMapper.toDomain(entityOpt.get());
        try {
            redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(domain), CACHE_TTL);
        } catch (Exception e) {
            log.error("Failed to cache event ID: {} in Redis", id, e);
        }

        return Optional.of(domain);
    }

    @Override
    public List<Event> findAll() {
        try {
            String cachedJson = redisTemplate.opsForValue().get(CACHE_KEY_ALL);
            if (cachedJson != null) {
                log.info("Cache hit for all events");
                return objectMapper.readValue(cachedJson, new TypeReference<List<Event>>() {});
            }
        } catch (Exception e) {
            log.error("Failed to query Redis cache for all events, querying DB directly", e);
        }

        log.info("Cache miss for all events. Querying database");
        List<EventEntity> entities = jpaEventRepository.findAll();
        List<Event> domains = entities.stream()
                .map(EventMapper::toDomain)
                .collect(Collectors.toList());

        try {
            redisTemplate.opsForValue().set(CACHE_KEY_ALL, objectMapper.writeValueAsString(domains), CACHE_TTL);
        } catch (Exception e) {
            log.error("Failed to cache all events in Redis", e);
        }

        return domains;
    }

    private void evictCache(Long id) {
        try {
            redisTemplate.delete(CACHE_KEY_PREFIX + id);
            redisTemplate.delete(CACHE_KEY_ALL);
            log.info("Evicted cache for event ID: {} and all-events cache list", id);
        } catch (Exception e) {
            log.error("Failed to evict Redis cache for event ID: {}", id, e);
        }
    }

    @Override
    public List<Event> findByNameAndLocation(String name, String location) {
        String queryName = name == null ? "" : name.trim();
        String queryLocation = location == null ? "" : location.trim();
        return jpaEventRepository.findByNameContainingIgnoreCaseAndLocationContainingIgnoreCase(queryName, queryLocation)
                .stream()
                .map(EventMapper::toDomain)
                .collect(Collectors.toList());
    }
}

package com.ticketpass.ticketservice.infrastructure.persistence;

import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.domain.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({PostgresEventRepository.class, com.fasterxml.jackson.databind.ObjectMapper.class})
public class EventServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("ticketpass")
            .withUsername("admin")
            .withPassword("admin");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @MockBean
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EventRepository eventRepository;

    @Test
    void shouldSaveAndFindEventSuccessfully() {
        Event event = Event.builder()
                .name("Test Concert")
                .description("A great concert")
                .dateTime(LocalDateTime.now().plusDays(10))
                .location("Sao Paulo")
                .totalTickets(500)
                .availableTickets(500)
                .price(BigDecimal.valueOf(100.0))
                .build();

        Event saved = eventRepository.save(event);
        assertThat(saved.getId()).isNotNull();

        Optional<Event> found = eventRepository.findByIdForUpdate(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Test Concert");
        assertThat(found.get().getAvailableTickets()).isEqualTo(500);
    }
}

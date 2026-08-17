package com.ticketpass.ticketservice.infrastructure.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpass.ticketservice.infrastructure.messaging.RabbitMQConfig;
import com.ticketpass.ticketservice.infrastructure.messaging.dto.ReservationCreatedEvent;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.OutboxEventEntity;
import com.ticketpass.ticketservice.infrastructure.persistence.repository.JpaOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {

    private final JpaOutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Scheduled(fixedDelay = 1000)
    public void publishOutboxEvents() {
        List<OutboxEventEntity> events = outboxEventRepository.findTop100ByOrderByIdAsc();
        if (events.isEmpty()) {
            return;
        }

        log.info("Found {} outbox events to publish", events.size());
        for (OutboxEventEntity event : events) {
            try {
                if ("RESERVATION_CREATED".equals(event.getEventType())) {
                    ReservationCreatedEvent payload = objectMapper.readValue(event.getPayload(), ReservationCreatedEvent.class);

                    log.info("Publishing outbox event ID {} to RabbitMQ: {}", event.getId(), payload);
                    rabbitTemplate.convertAndSend(
                            RabbitMQConfig.EXCHANGE_NAME,
                            RabbitMQConfig.BOOKING_REQUESTS_ROUTING_KEY,
                            payload
                    );
                }

                outboxEventRepository.delete(event);
                log.info("Successfully published and cleared outbox event ID: {}", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event ID: {}", event.getId(), e);
            }
        }
    }
}

package com.ticketpass.ticketservice.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketpass.ticketservice.application.port.MessagePublisher;
import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.infrastructure.messaging.dto.ReservationCreatedEvent;
import com.ticketpass.ticketservice.infrastructure.persistence.entity.OutboxEventEntity;
import com.ticketpass.ticketservice.infrastructure.persistence.repository.JpaOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMessagePublisher implements MessagePublisher {

    private final JpaOutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publishReservationCreated(Reservation reservation) {
        ReservationCreatedEvent event = new ReservationCreatedEvent(
                reservation.getId(),
                reservation.getEventId(),
                reservation.getQuantity(),
                reservation.getTotalAmount()
        );
        try {
            String payload = objectMapper.writeValueAsString(event);
            OutboxEventEntity outboxEvent = OutboxEventEntity.builder()
                    .aggregateType("RESERVATION")
                    .aggregateId(reservation.getId())
                    .eventType("RESERVATION_CREATED")
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .build();

            log.info("Saving reservation created event to Outbox: {}", event);
            outboxEventRepository.save(outboxEvent);
        } catch (Exception e) {
            log.error("Failed to serialize and save reservation created event to Outbox for reservation ID: {}", reservation.getId(), e);
            throw new RuntimeException("Outbox save failed", e);
        }
    }
}

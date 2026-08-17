package com.ticketpass.paymentservice.infrastructure.messaging.consumer;

import com.ticketpass.paymentservice.infrastructure.messaging.RabbitMQConfig;
import com.ticketpass.paymentservice.infrastructure.messaging.dto.PaymentStatusEvent;
import com.ticketpass.paymentservice.infrastructure.messaging.dto.ReservationCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingRequestConsumer {

    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;

    private static final String IDEMPOTENCY_KEY_PREFIX = "payment:processed:";
    private static final Duration IDEMPOTENCY_TTL = Duration.ofHours(24);

    @RabbitListener(queues = RabbitMQConfig.BOOKING_REQUESTS_QUEUE)
    public void consumeBookingRequest(ReservationCreatedEvent event) {
        log.info("Received booking request event: {}", event);
        String key = IDEMPOTENCY_KEY_PREFIX + event.reservationId();

        try {
            Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, "IN_PROGRESS", IDEMPOTENCY_TTL);
            if (Boolean.FALSE.equals(isNew)) {
                String previousStatus = redisTemplate.opsForValue().get(key);
                if ("IN_PROGRESS".equals(previousStatus)) {
                    log.warn("Another process is currently handling reservation ID: {}. Skipping.", event.reservationId());
                    return;
                }
                if (previousStatus != null) {
                    log.warn("Duplicate message detected for reservation ID: {}. Previous status: {}. Resending status event to ensure alignment.", 
                            event.reservationId(), previousStatus);
                    publishStatus(event.reservationId(), previousStatus);
                }
                return;
            }

            log.info("Simulating payment gateway processing for reservation ID: {}", event.reservationId());
            Thread.sleep(2000);

            String finalStatus = "APPROVED";
            if (event.quantity() == 5) {
                finalStatus = "FAILED";
                log.info("Simulating payment FAILURE for reservation ID: {} (Quantity is 5)", event.reservationId());
            } else {
                log.info("Simulating payment SUCCESS for reservation ID: {}", event.reservationId());
            }

            redisTemplate.opsForValue().set(key, finalStatus, IDEMPOTENCY_TTL);
            publishStatus(event.reservationId(), finalStatus);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Payment processing interrupted for reservation ID: {}", event.reservationId(), e);
        } catch (Exception e) {
            log.error("Failed to process payment for reservation ID: {}", event.reservationId(), e);
            redisTemplate.delete(key);
            throw e;
        }
    }

    private void publishStatus(Long reservationId, String status) {
        PaymentStatusEvent statusEvent = new PaymentStatusEvent(reservationId, status);
        log.info("Publishing payment status: {}", statusEvent);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.PAYMENT_STATUS_ROUTING_KEY,
                statusEvent
        );
    }
}

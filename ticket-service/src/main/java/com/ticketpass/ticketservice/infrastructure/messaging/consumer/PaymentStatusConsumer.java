package com.ticketpass.ticketservice.infrastructure.messaging.consumer;

import com.ticketpass.ticketservice.application.usecase.CancelReservationUseCase;
import com.ticketpass.ticketservice.application.usecase.ConfirmReservationUseCase;
import com.ticketpass.ticketservice.infrastructure.messaging.RabbitMQConfig;
import com.ticketpass.ticketservice.infrastructure.messaging.dto.PaymentStatusEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentStatusConsumer {

    private final ConfirmReservationUseCase confirmReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;

    @RabbitListener(queues = RabbitMQConfig.PAYMENT_STATUS_QUEUE)
    public void consumePaymentStatus(PaymentStatusEvent event) {
        log.info("Received payment status event: {}", event);
        try {
            if ("APPROVED".equalsIgnoreCase(event.status())) {
                confirmReservationUseCase.execute(event.reservationId());
                log.info("Reservation ID {} successfully CONFIRMED", event.reservationId());
            } else {
                cancelReservationUseCase.execute(event.reservationId());
                log.info("Reservation ID {} successfully CANCELED and inventory released", event.reservationId());
            }
        } catch (Exception e) {
            log.error("Failed to process payment status update for reservation ID: {}", event.reservationId(), e);
            throw e;
        }
    }
}

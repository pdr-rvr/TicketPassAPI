package com.ticketpass.paymentservice.infrastructure.messaging.dto;

import java.math.BigDecimal;

public record ReservationCreatedEvent(
    Long reservationId,
    Long eventId,
    Integer quantity,
    BigDecimal totalAmount
) {}

package com.ticketpass.ticketservice.infrastructure.messaging.dto;

import java.math.BigDecimal;

public record ReservationCreatedEvent(
    Long reservationId,
    Long eventId,
    Integer quantity,
    BigDecimal totalAmount
) {}

package com.ticketpass.ticketservice.infrastructure.messaging.dto;

public record PaymentStatusEvent(
    Long reservationId,
    String status
) {}

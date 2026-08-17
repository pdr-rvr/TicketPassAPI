package com.ticketpass.paymentservice.infrastructure.messaging.dto;

public record PaymentStatusEvent(
    Long reservationId,
    String status
) {}

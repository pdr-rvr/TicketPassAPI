package com.ticketpass.ticketservice.interfaces.rest.dto;

import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.domain.model.ReservationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
    Long id,
    Long eventId,
    Long userId,
    Integer quantity,
    BigDecimal totalAmount,
    ReservationStatus status,
    LocalDateTime createdAt,
    LocalDateTime expiresAt
) {
    public static ReservationResponse fromDomain(Reservation reservation) {
        if (reservation == null) return null;
        return new ReservationResponse(
            reservation.getId(),
            reservation.getEventId(),
            reservation.getUserId(),
            reservation.getQuantity(),
            reservation.getTotalAmount(),
            reservation.getStatus(),
            reservation.getCreatedAt(),
            reservation.getExpiresAt()
        );
    }
}

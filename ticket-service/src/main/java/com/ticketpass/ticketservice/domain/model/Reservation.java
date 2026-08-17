package com.ticketpass.ticketservice.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    private Long id;
    private Long eventId;
    private Long userId;
    private Integer quantity;
    private BigDecimal totalAmount;
    private ReservationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public void confirm() {
        if (this.status == ReservationStatus.CONFIRMED) {
            return;
        }
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be confirmed");
        }
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        if (this.status == ReservationStatus.CANCELED) {
            return;
        }
        if (this.status != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be canceled");
        }
        this.status = ReservationStatus.CANCELED;
    }

    public boolean isExpired() {
        return this.status == ReservationStatus.PENDING && LocalDateTime.now().isAfter(this.expiresAt);
    }
}

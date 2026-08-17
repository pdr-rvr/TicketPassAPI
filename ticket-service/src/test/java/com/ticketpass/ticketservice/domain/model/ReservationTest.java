package com.ticketpass.ticketservice.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ReservationTest {

    @Test
    void shouldConfirmPendingReservationSuccessfully() {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.PENDING)
                .build();

        reservation.confirm();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void shouldBeIdempotentWhenConfirmingAlreadyConfirmedReservation() {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.CONFIRMED)
                .build();

        // Should not throw exception, just do nothing
        reservation.confirm();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void shouldThrowExceptionWhenConfirmingCanceledReservation() {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.CANCELED)
                .build();

        assertThatThrownBy(reservation::confirm)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only pending reservations can be confirmed");
    }

    @Test
    void shouldCancelPendingReservationSuccessfully() {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.PENDING)
                .build();

        reservation.cancel();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    void shouldBeIdempotentWhenCancelingAlreadyCanceledReservation() {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.CANCELED)
                .build();

        // Should not throw exception
        reservation.cancel();
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CANCELED);
    }

    @Test
    void shouldThrowExceptionWhenCancelingConfirmedReservation() {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatus.CONFIRMED)
                .build();

        assertThatThrownBy(reservation::cancel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only pending reservations can be canceled");
    }
}

package com.ticketpass.ticketservice.application.port;

import com.ticketpass.ticketservice.domain.model.Reservation;

public interface MessagePublisher {
    void publishReservationCreated(Reservation reservation);
}

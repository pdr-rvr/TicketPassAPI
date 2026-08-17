package com.ticketpass.ticketservice.domain.repository;

import com.ticketpass.ticketservice.domain.model.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
    Reservation save(Reservation reservation);
    Optional<Reservation> findById(Long id);
    List<Reservation> findAllExpired(LocalDateTime now);
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByEventId(Long eventId);
    int cancelAllActiveByEventId(Long eventId);
}

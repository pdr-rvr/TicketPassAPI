package com.ticketpass.ticketservice.infrastructure.scheduler;

import com.ticketpass.ticketservice.application.usecase.CancelReservationUseCase;
import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExpiredReservationsCleanupScheduler {

    private final ReservationRepository reservationRepository;
    private final CancelReservationUseCase cancelReservationUseCase;

    @Scheduled(fixedDelay = 10000)
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<Reservation> expired = reservationRepository.findAllExpired(now);
        if (!expired.isEmpty()) {
            log.info("Found {} expired reservations to clean up", expired.size());
            for (Reservation reservation : expired) {
                try {
                    cancelReservationUseCase.execute(reservation.getId());
                    log.info("Successfully cleaned up and released stock for expired reservation ID: {}", reservation.getId());
                } catch (Exception e) {
                    log.error("Failed to clean up expired reservation ID: {}", reservation.getId(), e);
                }
            }
        }
    }
}

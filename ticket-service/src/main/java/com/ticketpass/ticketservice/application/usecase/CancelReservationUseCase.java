package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.exception.ResourceNotFoundException;
import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.domain.model.ReservationStatus;
import com.ticketpass.ticketservice.domain.repository.EventRepository;
import com.ticketpass.ticketservice.domain.repository.ReservationRepository;
import com.ticketpass.ticketservice.domain.service.LockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelReservationUseCase {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final LockService lockService;
    private final TransactionTemplate transactionTemplate;

    public void execute(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        if (reservation.getStatus() == ReservationStatus.CANCELED) {
            return;
        }

        String lockKey = "lock:event:" + reservation.getEventId();

        lockService.executeWithLock(lockKey, 10, TimeUnit.SECONDS, () -> {
            transactionTemplate.executeWithoutResult(status -> {
                Reservation innerRes = reservationRepository.findById(reservationId)
                        .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

                if (innerRes.getStatus() == ReservationStatus.CANCELED) {
                    return;
                }

                if (innerRes.getStatus() != ReservationStatus.PENDING) {
                    log.warn("Reservation ID {} cannot be canceled because its status is {}", reservationId, innerRes.getStatus());
                    return;
                }

                Event event = eventRepository.findByIdForUpdate(innerRes.getEventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

                // Return tickets to stock
                event.releaseTickets(innerRes.getQuantity());
                eventRepository.save(event);

                // Cancel reservation
                innerRes.cancel();
                reservationRepository.save(innerRes);
            });
            return null;
        });
    }
}

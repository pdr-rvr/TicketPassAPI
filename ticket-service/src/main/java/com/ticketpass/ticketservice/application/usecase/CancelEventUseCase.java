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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelEventUseCase {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final LockService lockService;
    private final TransactionTemplate transactionTemplate;

    public void execute(Long eventId) {
        String lockKey = "lock:event:" + eventId;

        lockService.executeWithLock(lockKey, 15, TimeUnit.SECONDS, () -> {
            transactionTemplate.executeWithoutResult(status -> {
                Event event = eventRepository.findByIdForUpdate(eventId)
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + eventId));

                if ("CANCELED".equalsIgnoreCase(event.getStatus())) {
                    log.warn("Event ID {} is already canceled", eventId);
                    return;
                }

                // Cancel the event in domain
                event.cancel();
                eventRepository.save(event);

                // Bulk cancel all active reservations for the event
                int canceledCount = reservationRepository.cancelAllActiveByEventId(eventId);
                log.info("Bulk canceled {} active reservations for event ID {}", canceledCount, eventId);
            });
            return null;
        });
    }
}

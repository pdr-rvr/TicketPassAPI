package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.application.port.MessagePublisher;
import com.ticketpass.ticketservice.domain.exception.ResourceNotFoundException;
import com.ticketpass.ticketservice.domain.model.Event;
import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.domain.model.ReservationStatus;
import com.ticketpass.ticketservice.domain.repository.EventRepository;
import com.ticketpass.ticketservice.domain.repository.ReservationRepository;
import com.ticketpass.ticketservice.domain.repository.UserRepository;
import com.ticketpass.ticketservice.domain.service.LockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ReserveTicketsUseCase {

    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final LockService lockService;
    private final MessagePublisher messagePublisher;
    private final TransactionTemplate transactionTemplate;

    public Reservation execute(Command command) {
        // Validate user existence before starting database transaction or lock
        com.ticketpass.ticketservice.domain.model.User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + command.userId()));

        if (command.authenticatedEmail() != null && !command.isAdmin()) {
            if (!user.getEmail().equalsIgnoreCase(command.authenticatedEmail())) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to create reservations for another user");
            }
        }

        String lockKey = "lock:event:" + command.eventId();

        // 1. Acquire distributed lock to prevent concurrent modifications on event inventory
        return lockService.executeWithLock(lockKey, 10, TimeUnit.SECONDS, () -> {
            // 2. Perform the database modifications inside a transaction bound by the lock
            return transactionTemplate.execute(status -> {
                Event event = eventRepository.findByIdForUpdate(command.eventId())
                        .orElseThrow(() -> new ResourceNotFoundException("Event not found with ID: " + command.eventId()));

                // Update event ticket stock (domain method throws Exception if canceled or sold out)
                event.reserveTickets(command.quantity());
                eventRepository.save(event);

                // Create reservation
                Reservation pendingReservation = Reservation.builder()
                        .eventId(event.getId())
                        .userId(command.userId())
                        .quantity(command.quantity())
                        .totalAmount(event.getPrice().multiply(BigDecimal.valueOf(command.quantity())))
                        .status(ReservationStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusMinutes(5)) // TTL of 5 minutes
                        .build();

                Reservation savedReservation = reservationRepository.save(pendingReservation);

                // 3. Publish outbox event to DB inside the transaction to guarantee atomic consistency
                messagePublisher.publishReservationCreated(savedReservation);

                return savedReservation;
            });
        });
    }

    public record Command(Long eventId, Long userId, Integer quantity, String authenticatedEmail, boolean isAdmin) {
        public Command(Long eventId, Long userId, Integer quantity) {
            this(eventId, userId, quantity, null, true);
        }
    }
}

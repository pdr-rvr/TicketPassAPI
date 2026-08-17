package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.exception.ResourceNotFoundException;
import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetReservationUseCase {

    private final ReservationRepository reservationRepository;
    private final com.ticketpass.ticketservice.domain.repository.UserRepository userRepository;

    public Reservation getById(Long id) {
        return getById(id, null, true);
    }

    public Reservation getById(Long id, String authenticatedEmail, boolean isAdmin) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + id));

        if (authenticatedEmail != null && !isAdmin) {
            com.ticketpass.ticketservice.domain.model.User user = userRepository.findById(reservation.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + reservation.getUserId()));
            if (!user.getEmail().equalsIgnoreCase(authenticatedEmail)) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view this reservation");
            }
        }

        return reservation;
    }

    public List<Reservation> getByUserId(Long userId) {
        return getByUserId(userId, null, true);
    }

    public List<Reservation> getByUserId(Long userId, String authenticatedEmail, boolean isAdmin) {
        if (authenticatedEmail != null && !isAdmin) {
            com.ticketpass.ticketservice.domain.model.User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
            if (!user.getEmail().equalsIgnoreCase(authenticatedEmail)) {
                throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view reservations of another user");
            }
        }
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getByEventId(Long eventId) {
        return reservationRepository.findByEventId(eventId);
    }
}

package com.ticketpass.ticketservice.application.usecase;

import com.ticketpass.ticketservice.domain.exception.ResourceNotFoundException;
import com.ticketpass.ticketservice.domain.model.Reservation;
import com.ticketpass.ticketservice.domain.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ConfirmReservationUseCase {

    private final ReservationRepository reservationRepository;

    @Transactional
    public void execute(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with ID: " + reservationId));

        reservation.confirm();
        reservationRepository.save(reservation);
    }
}

package com.ticketpass.ticketservice.interfaces.rest;

import com.ticketpass.ticketservice.application.usecase.CancelReservationUseCase;
import com.ticketpass.ticketservice.application.usecase.GetReservationUseCase;
import com.ticketpass.ticketservice.application.usecase.ReserveTicketsUseCase;
import com.ticketpass.ticketservice.interfaces.rest.dto.ReservationResponse;
import com.ticketpass.ticketservice.interfaces.rest.dto.ReserveTicketsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReserveTicketsUseCase reserveTicketsUseCase;
    private final GetReservationUseCase getReservationUseCase;
    private final CancelReservationUseCase cancelReservationUseCase;

    @PostMapping
    public ResponseEntity<ReservationResponse> reserveTickets(
            @Valid @RequestBody ReserveTicketsRequest request,
            Authentication authentication
    ) {
        String userEmail = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        ReserveTicketsUseCase.Command command = new ReserveTicketsUseCase.Command(
                request.eventId(),
                request.userId(),
                request.quantity(),
                userEmail,
                isAdmin
        );
        ReservationResponse response = ReservationResponse.fromDomain(reserveTicketsUseCase.execute(command));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponse> getReservation(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String userEmail = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        ReservationResponse response = ReservationResponse.fromDomain(getReservationUseCase.getById(id, userEmail, isAdmin));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationResponse>> getReservationsByUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        String userEmail = authentication != null ? authentication.getName() : null;
        boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        List<ReservationResponse> response = getReservationUseCase.getByUserId(userId, userEmail, isAdmin).stream()
                .map(ReservationResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(@PathVariable Long id) {
        cancelReservationUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
